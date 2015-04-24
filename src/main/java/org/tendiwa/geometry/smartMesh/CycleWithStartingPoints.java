package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.MutableShreddedSegment2D;

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

	Set<MutableShreddedSegment2D> snapStartingPoints(Map<Segment2D, List<Point2D>> pointsOnPolygonBorder) {
		return pointsOnPolygonBorder.entrySet()
			.stream()
			.map(e -> new MutableShreddedSegment2D(e.getKey(), e.getValue()))
			.map(this::snapPointsToOriginalSegmentEnds)
			.collect(toImmutableSet());
	}

	private MutableShreddedSegment2D snapPointsToOriginalSegmentEnds(MutableShreddedSegment2D shreddedSegment) {
		List<Point2D> points = shreddedSegment.pointStream()
			.map(point -> snapToSegmentEnd(point, shreddedSegment.originalSegment()))
			.collect(toImmutableList());
		return new MutableShreddedSegment2D(
			shreddedSegment.originalSegment(),
			points
		);
	}

	private Point2D snapToSegmentEnd(Point2D startingPoint, Segment2D edge) {
		double toStart = startingPoint.squaredDistanceTo(edge.start());
		double toEnd = startingPoint.squaredDistanceTo(edge.end());
		double snapSizeSquared = config.snapSize * config.snapSize;
		if (toStart < toEnd) {
			if (toStart < snapSizeSquared) {
				return edge.start();
			}
		} else {
			if (toEnd < snapSizeSquared) {
				return edge.end();
			}
		}
		return startingPoint;
	}
}