package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.*;
import java.util.stream.Collectors;

public class ApproximatedEdges {
	/**
	 * Chebyshov distance in which points are considered equal.
	 */
	private final TreeSet<Point2D> byX = new TreeSet<>(
		(a, b) -> (int) (10 * Math.signum(a.x - b.x) + 5 * Math.signum(a.y - b.y))
	);
	private final TreeSet<Point2D> byY = new TreeSet<>(
		(a, b) -> (int) (10 * Math.signum(a.y - b.y) + 5 * Math.signum(a.x - b.x))
	);
	public final List<Segment2D> edges = new LinkedList<>();

	public void addFixedEdge(Segment2D edge) {
		edge = new Segment2D(
			snapPointToExistingPoints(edge.start),
			snapPointToExistingPoints(edge.end)
		);
		// Already contained elements just won't be added.
		byX.add(edge.start);
		byY.add(edge.start);
		byX.add(edge.end);
		byY.add(edge.end);
		edges.add(edge);
	}

	/**
	 * Snaps a point to an existing point in its {@link org.tendiwa.geometry.Vectors2D#EPSILON}-neighborhood if there
	 * is one.
	 *
	 * @param point
	 * 	A point.
	 * @return If among all points exists a point that is closer than {@link org.tendiwa.geometry.Vectors2D#EPSILON} in
	 * Chebyshov distance to {@code point}, then returns that closer point, otherwise returns {@code point}.
	 */
	private Point2D snapPointToExistingPoints(Point2D point) {
		// TODO: This should probably be rewritten without Streams API for better performance.
		Set<Point2D> closePoints = ImmutableSet
			.<Point2D>builder()
			.addAll(
				byX.subSet(point.moveBy(-Vectors2D.EPSILON, 0), point.moveBy(Vectors2D.EPSILON, 0))
			)
			.addAll(
				byY.subSet(point.moveBy(0, -Vectors2D.EPSILON), point.moveBy(0, Vectors2D.EPSILON))
			)
			.build()
			.stream()
			.filter(p -> p.chebyshovDistanceTo(point) < Vectors2D.EPSILON)
			.collect(Collectors.toSet());
		if (closePoints.size() == 1) {
			return closePoints.iterator().next();
		} else if (closePoints.isEmpty()) {
			return point;
		}
		throw new GeometryException("More than one point found");
	}
}
