package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.ShreddedSegment2D;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.tendiwa.collections.Collectors.toImmutableList;
import static org.tendiwa.collections.Collectors.toImmutableSet;

final class CycleWithStartingPoints {

	private final NetworkGenerationParameters config;

	public CycleWithStartingPoints(
		NetworkGenerationParameters config
	) {
		this.config = config;
	}

	Set<ShreddedSegment2D> snapStartingPoints(Map<Segment2D, List<Point2D>> pointsOnPolygonBorder) {
		return pointsOnPolygonBorder.entrySet()
			.stream()
			.map(e -> new ShreddedSegment2D(e.getKey(), e.getValue()))
			.map(this::snapPointsToOriginalSegmentEnds)
			.collect(toImmutableSet());
	}

	private ShreddedSegment2D snapPointsToOriginalSegmentEnds(ShreddedSegment2D shreddedSegment) {
		List<Point2D> points = shreddedSegment.pointStream()
			.map(point -> snapToSegmentEnd(point, shreddedSegment.originalSegment()))
			.collect(toImmutableList());
		return new ShreddedSegment2D(
			shreddedSegment.originalSegment(),
			points
		);
	}

	/**
	 * If {@code startingPoint} can be snapped to one or both ends of {@code edge}, returns the closest of ends.
	 * Otherwise returns {@code startingPoint}.
	 *
	 * @param startingPoint
	 * 	A point to snap.
	 * @param edge
	 * 	An edge to whose ends to snap.
	 * @return Closest snappable end of {@code edge} or {@code startingPoint} if it is not close enough to either of
	 * edges.
	 */
	private Point2D snapToSegmentEnd(Point2D startingPoint, Segment2D edge) {
		double toStart = startingPoint.squaredDistanceTo(edge.start);
		double toEnd = startingPoint.squaredDistanceTo(edge.end);
		double snapSizeSquared = config.snapSize * config.snapSize;
		if (toStart < toEnd) {
			if (toStart < snapSizeSquared) {
				return edge.start;
			}
		} else {
			if (toEnd < snapSizeSquared) {
				return edge.end;
			}
		}
		return startingPoint;
	}
}