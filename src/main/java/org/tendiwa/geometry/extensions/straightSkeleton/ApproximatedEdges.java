package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.*;

public class ApproximatedEdges {
	/**
	 * Chebyshov distance in which points are considered equal.
	 */
	private static final double EPSILON = 1e-10;
	private final TreeSet<Point2D> startByX = new TreeSet<>(
		(a, b) -> (int) (10 * Math.signum(a.x - b.x) + 5 * Math.signum(a.y - b.y))
	);
	private final TreeSet<Point2D> startByY = new TreeSet<>(
		(a, b) -> (int) (10 * Math.signum(a.y - b.y) + 5 * Math.signum(a.x - b.x))
	);
	private final TreeSet<Point2D> endByX = new TreeSet<>((a, b) -> (int) Math.signum(a.x - b.x));
	private final TreeSet<Point2D> endByY = new TreeSet<>((a, b) -> (int) Math.signum(a.y - b.y));
	public final List<Segment2D> edges = new LinkedList<>();

	public void addFixedEdge(Segment2D edge) {
		edge = new Segment2D(
			snapPointToExistingPoints(edge.start, startByX, startByY),
			snapPointToExistingPoints(edge.end, startByX, startByY)
		);
		// Already contained elements just won't be added.
		startByX.add(edge.start);
		startByY.add(edge.start);
		startByX.add(edge.end);
		startByY.add(edge.end);
//		endByX.add(edge.end);
//		endByY.add(edge.end);
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
	 * 	Other existing points sorted by y coordinate.
	 * @return If among all points exists a point that is closer than {@link #EPSILON} in Chebyshov distance to
	 * {@code point}, then returns that closer point, otherwise returns {@code point}.
	 */
	private Point2D snapPointToExistingPoints(Point2D point, TreeSet<Point2D> byX, TreeSet<Point2D> byY) {
		Point2D xLower = byX.lower(point);
		Point2D xHigher = byX.higher(point);
		Point2D yLower = byY.lower(point);
		Point2D yHigher = byY.higher(point);
		Point2D snapPoint = null;
		if (xLower != null) {
			snapPoint = chooseSnapPoint(snapPoint, xLower, point);
		}
		if (yLower != null) {
			snapPoint = chooseSnapPoint(snapPoint, yLower, point);
		}
		if (xHigher != null) {
			snapPoint = chooseSnapPoint(snapPoint, xHigher, point);
		}
		if (yHigher != null) {
			snapPoint = chooseSnapPoint(snapPoint, yHigher, point);
		}
		if (snapPoint == null) {
			assert isNotTooClose(point, xLower);
			assert isNotTooClose(point, xHigher);
			assert isNotTooClose(point, yHigher);
			assert isNotTooClose(point, yLower);
			return point;
		}
		if (Math.abs(snapPoint.x - point.x) < EPSILON && Math.abs(snapPoint.y - point.y) < EPSILON) {
			return snapPoint;
		} else {
			assert isNotTooClose(point, xLower);
			assert isNotTooClose(point, xHigher);
			assert isNotTooClose(point, yHigher);
			assert isNotTooClose(point, yLower);
			return point;
		}
	}

	private Point2D chooseSnapPoint(Point2D snapPoint, Point2D newSnapPoint, Point2D realEnd) {
		assert newSnapPoint != null;
		if (snapPoint == null) {
			return newSnapPoint;
		}
		if (snapPoint.chebyshevDistanceTo(realEnd) > newSnapPoint.chebyshevDistanceTo(realEnd)) {
			return newSnapPoint;
		}
		return snapPoint;
	}

	private boolean isNotTooClose(Point2D point, Point2D existingPoint) {
		if (existingPoint == null) {
			return true;
		}
		return Math.abs(existingPoint.x - point.x) >= EPSILON || Math.abs(existingPoint.y - point.y) >= EPSILON;
	}
}
