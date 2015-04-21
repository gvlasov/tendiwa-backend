package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;

import java.util.*;
import java.util.stream.Stream;

import static org.tendiwa.collections.Collectors.toLinkedHashSet;

final class FloodFromOuterCycle {
	private final OrientedCycle outerCycle;
	private final NetworkGenerationParameters config;
	private final Random random;

	FloodFromOuterCycle(
		OrientedCycle outerCycle,
		NetworkGenerationParameters config,
		Random random
	) {
		this.outerCycle = outerCycle;
		this.config = config;
		this.random = random;
	}

	Set<FloodStart> floods() {
		Map<Segment2D, List<Point2D>> points = pointsAtRegularIntervals(outerCycle);
		return snapShredsToSegmentEnds(points)
			.stream()
			.flatMap(this::toDistinctPointsOnSegment)
			.map(this::createFloodStart)
			.collect(toLinkedHashSet());
	}

	private Map<Segment2D, List<Point2D>> pointsAtRegularIntervals(OrientedCycle cycle) {
		return IntervalsAlongPolygonBorder.compute(
			cycle.vertexList(),
			config.segmentLength,
			config.innerNetworkSegmentLengthDeviation,
			cycle.graph()::getEdge,
			random
		);
	}

	private Stream<SplitSegment2D> toDistinctPointsOnSegment(CutSegment2D cutSegment) {
		return cutSegment
			.pointStream()
			.map(p -> new SplitSegment2D(cutSegment.originalSegment(), p));
	}

	private Set<MutableShreddedSegment2D> snapShredsToSegmentEnds(Map<Segment2D, List<Point2D>> points) {
		return new CycleWithStartingPoints(config).snapStartingPoints(points);
	}

	private FloodStart createFloodStart(SplitSegment2D splitSegment) {
		return new FloodStart(
			normal(splitSegment),
			sector(splitSegment),
			enclosingSegment(splitSegment)
		);
	}

	private Sector sector(SplitSegment2D splitSegment) {
		if (outerCycle.graph().containsVertex(splitSegment.middlePoint())) {
			return new OrientedCycleSector(outerCycle, splitSegment.middlePoint(), true);
		} else {
			return new OrientedCycleEdgeHalfplane(outerCycle, splitSegment, true);
		}
	}

	private Ray normal(SplitSegment2D splitSegment) {
		if (outerCycle.graph().containsVertex(splitSegment.middlePoint())) {
			return outerCycle.deviatedAngleBisector(splitSegment.middlePoint(), true);
		} else {
			assert outerCycle.graph().containsEdge(splitSegment.originalSegment());
			return outerCycle.normal(splitSegment, true);
		}
	}

	private Optional<Segment2D> enclosingSegment(SplitSegment2D splitSegment) {
		Segment2D segment = splitSegment.originalSegment();
		Point2D point = splitSegment.middlePoint();
		return segment.oneOfEndsIs(point) ? Optional.empty() : Optional.of(segment);
	}
}