package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Ray;
import org.tendiwa.geometry.Sector;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.SplitSegment2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.util.Optional;
import java.util.Random;

final class InnerNetwork {
	private final MutableGraph2D fullGraph;
	private final NetworkGenerationParameters config;
	private final Random random;
	private final CycleWithInnerCycles perforatedCycle;
	private final CycleEdges cycleEdges;

	InnerNetwork(
		CycleWithInnerCycles perforatedCycle,
		CycleEdges cycleEdges,
		MutableGraph2D fullGraph,
		NetworkGenerationParameters config,
		Random random
	) {
		this.perforatedCycle = perforatedCycle;
		this.fullGraph = fullGraph;
		this.config = config;
		this.random = random;
		this.cycleEdges = cycleEdges;
	}


	Optional<Ray> tryPlacingSegment(Ray beginning, Sector allowedSector) {
		double segmentLength = deviatedLength();
		assert fullGraph.vertexSet().contains(beginning.start);
		PropagationEvent event = new SnapTest(
			config.snapSize,
			beginning.start,
			beginning.placeEnd(segmentLength),
			fullGraph,
			allowedSector
		).snap();
		assert event.createsNewSegment();
		integrateIntoNetwork(event);
		if (event.isTerminal()) {
			return Optional.empty();
		} else {
			return Optional.of(beginning.changeStart(event.target()));
		}
	}

	Optional<Ray> tryPlacingFirstSegment(FloodStart floodStart) {
		if (!floodStart.holdingSegment.isPresent()) {
			assert fullGraph.containsVertex(floodStart.rootRay.start);
		}
		return tryPlacingSegment(floodStart.rootRay, floodStart.rootSector);
	}


	private double deviatedLength() {
		return config.segmentLength - config.innerNetworkSegmentLengthDeviation / 2 + random.nextDouble() *
			config.innerNetworkSegmentLengthDeviation;
	}

	private void integrateIntoNetwork(PropagationEvent event) {
		Segment2D newSegment = event.addedSegment();

		Optional<Segment2D> splitMaybe = event.splitSegmentMaybe();
		if (splitMaybe.isPresent()) {
			Segment2D edge = splitMaybe.get();
			if (cycleEdges.isShared(edge)) {
				cycleEdges.splitSharedEdge(new SplitSegment2D(edge, event.target()));
			} else {
				fullGraph.integrateCutSegment(new SplitSegment2D(edge, newSegment.end()));
			}
		}

		fullGraph.addVertex(newSegment.end());
		fullGraph.addSegmentAsEdge(newSegment);

		NonIntersectionTest.test(fullGraph, newSegment);
	}
}