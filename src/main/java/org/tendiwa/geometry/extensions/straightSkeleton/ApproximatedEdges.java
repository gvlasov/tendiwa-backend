package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.*;

public class ApproximatedEdges {
	/**
	 * Chebyshov distance in which points are considered equal.
	 */
	private static final double EPSILON = 1e-10;
	private final TreeSet<Point2D> startByX = new TreeSet<>((a, b) -> (int) Math.signum(a.x - b.x));
	private final TreeSet<Point2D> startByY = new TreeSet<>((a, b) -> (int) Math.signum(a.y - b.y));
	private final TreeSet<Point2D> endByX = new TreeSet<>((a, b) -> (int) Math.signum(a.x - b.x));
	private final TreeSet<Point2D> endByY = new TreeSet<>((a, b) -> (int) Math.signum(a.y - b.y));
	public final List<Segment2D> edges = new LinkedList<>();

	public void addFixedEdge(Segment2D edge) {
		edge = new Segment2D(
			snapPointToExistingPoints(edge.start, endByX, endByY),
			snapPointToExistingPoints(edge.end, startByX, startByY)
		);
		// Already contained elements just won't be added.
		startByX.add(edge.start);
		startByY.add(edge.end);
		endByX.add(edge.start);
		endByX.add(edge.end);
		edges.add(edge);
	}

	/**
	 * Snaps a point to an existing point in its {@link #EPSILON}-neighborhood if there is one.
	 *
	 * @param point
	 * 	A point.
	 * @param byX
	 * 	Other existing points sorted by x coordinate.
	 * @param byY
	 * 	Other existing points sorted by y corodinate.
	 * @return If among all points exists a point that is closer than {@link #EPSILON} in Chebyshov distance to
	 * {@code point}, then returns that closer point, otherwise returns {@code point}.
	 */
	private Point2D snapPointToExistingPoints(Point2D point, TreeSet<Point2D> byX, TreeSet<Point2D> byY) {
		Point2D xLower = byX.lower(point);
		Point2D xHigher = byX.higher(point);
		Point2D yLower = byY.lower(point);
		Point2D yHigher = byY.higher(point);
		Point2D yPoint, xPoint;
		if (yLower != null && yLower.equals(xLower)) {
			xPoint = xLower;
			yPoint = yLower;
		} else if (yLower != null && yLower.equals(xHigher)) {
			xPoint = xHigher;
			yPoint = yLower;
		} else if (yHigher != null && yHigher.equals(xLower)) {
			xPoint = xLower;
			yPoint = yHigher;
		} else if (yHigher != null && yHigher.equals(xHigher)) {
			xPoint = xHigher;
			yPoint = yHigher;
		} else {
			return point;
		}
		if (Math.abs(xPoint.x - point.x) < EPSILON && Math.abs(yPoint.y - point.y) < EPSILON) {
			return yPoint;
		} else {
			return point;
		}
	}
}
