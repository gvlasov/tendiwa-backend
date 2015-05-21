package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.geometry.graphs2d.Cycle2D;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static org.tendiwa.collections.Collectors.toImmutableList;

final class CycleWithStartingPoints {

	private final Cycle2D outerCycle;
	private final NetworkGenerationParameters config;
	private final Random random;

	public CycleWithStartingPoints(
		Cycle2D outerCycle,
		NetworkGenerationParameters config,
		Random random
	) {
		this.outerCycle = outerCycle;
		this.config = config;
		this.random = random;
	}

	Stream<SplitSegment2D> startingPointsOnSegments() {
		return snapStartingPoints(pointsAtRegularIntervals(outerCycle))
			.flatMap(this::toDistinctPointsOnSegment);
	}

	private Map<Segment2D, List<Point2D>> pointsAtRegularIntervals(Cycle2D cycle) {
		return IntervalsAlongPolygonBorder.compute(
			cycle,
			config.segmentLength,
			config.innerNetworkSegmentLengthDeviation,
			cycle::getEdge,
			random
		);
	}

	private Stream<MutableShreddedSegment2D> snapStartingPoints(
		Map<Segment2D, List<Point2D>> pointsOnPolygonBorder
	) {
		return pointsOnPolygonBorder.entrySet()
			.stream()
			.map(e -> new MutableShreddedSegment2D(e.getKey(), e.getValue()))
			.map(this::snapPointsToOriginalSegmentEnds);
	}

	private Stream<SplitSegment2D> toDistinctPointsOnSegment(CutSegment2D cutSegment) {
		return cutSegment
			.pointStream()
			.map(p -> new SplitSegment2D(cutSegment.originalSegment(), p));
	}

	private MutableShreddedSegment2D snapPointsToOriginalSegmentEnds(
		MutableShreddedSegment2D shreddedSegment
	) {
		return new MutableShreddedSegment2D(
			shreddedSegment.originalSegment(),
			shreddedSegment.pointStream()
				.map(point -> snapToSegmentEnd(point, shreddedSegment.originalSegment()))
				.collect(toImmutableList())
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