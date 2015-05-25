package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.*;

import java.util.*;

import static org.tendiwa.collections.Collectors.toLinkedHashSet;

final class FloodFromOuterCycle {
	private final MeshedNetworkCycle outerCycle;
	private final NetworkGenerationParameters config;
	private final Random random;

	FloodFromOuterCycle(
		MeshedNetworkCycle outerCycle,
		NetworkGenerationParameters config,
		Random random
	) {
		this.outerCycle = outerCycle;
		this.config = config;
		this.random = random;
	}

	Set<FloodStart> floods() {
		return new CycleWithStartingPoints(
			outerCycle,
			config,
			random
		)
			.startingPointsOnSegments()
			.map(this::createFloodStart)
			.collect(toLinkedHashSet());
	}


	private FloodStart createFloodStart(SplitSegment2D splitSegment) {
		return new FloodStart(
			normal(splitSegment),
			sector(splitSegment),
			enclosingSegment(splitSegment)
		);
	}

	private Sector sector(SplitSegment2D splitSegment) {
		if (outerCycle.containsVertex(splitSegment.middlePoint())) {
			return new OrientedCycleSector(outerCycle, splitSegment.middlePoint(), true);
		} else {
			return new OrientedCycleEdgeHalfplane(outerCycle, splitSegment, true);
		}
	}

	private Ray normal(SplitSegment2D splitSegment) {
		if (outerCycle.containsVertex(splitSegment.middlePoint())) {
			return outerCycle.deviatedAngleBisector(splitSegment.middlePoint(), true);
		} else {
			assert outerCycle.containsEdge(splitSegment.originalSegment());
			return outerCycle.normal(splitSegment, true);
		}
	}

	private Optional<Segment2D> enclosingSegment(SplitSegment2D splitSegment) {
		Segment2D segment = splitSegment.originalSegment();
		Point2D point = splitSegment.middlePoint();
		return segment.oneOfEndsIs(point) ? Optional.empty() : Optional.of(segment);
	}
}