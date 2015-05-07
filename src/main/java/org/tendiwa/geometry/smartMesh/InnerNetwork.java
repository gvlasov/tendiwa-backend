package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.Graphs;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Sector;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.SplitSegment2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

final class InnerNetwork {
	private final MutableGraph2D fullGraph;
	private final OrientedCycle outerCycle;
	private final Set<OrientedCycle> innerCycles;
	private final NetworkGenerationParameters config;
	private final Random random;
	private final DeadEndSet deadEnds;
	private final ExitsOnCycles branchEnds;

	InnerNetwork(
		OrientedCycle outerCycle,
		Set<OrientedCycle> innerCycles,
		NetworkGenerationParameters config,
		Random random
	) {
		this.outerCycle = outerCycle;
		this.innerCycles = innerCycles;
		this.config = config;
		this.random = random;
		this.deadEnds = new DeadEndSet();
		this.branchEnds = new ExitsOnCycles(config);
		this.fullGraph = initFullGraph();
	}

	private MutableGraph2D initFullGraph() {
		MutableGraph2D fullGraph = new MutableGraph2D();
		Graphs.addGraph(fullGraph, outerCycle.graph());
		innerCycles.forEach(cycle -> Graphs.addGraph(fullGraph, cycle.graph()));
		return fullGraph;
	}

	MutableGraph2D fullGraph() {
		return fullGraph;
	}

	Stream<CutSegment2D> whereBranchesStuckIntoCycles() {
		return branchEnds.getPartitionedSegments();
	}

	ImmutableSet<Segment2D> removableSegments() {
		return ImmutableSet.copyOf(deadEnds.values());
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
		if (floodStart.holdingSegment.isPresent()) {
			branchEnds.addOnSegment(floodStart.rootRay.start, floodStart.holdingSegment.get());
		} else {
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
			if (edgeIsOnCycles(edge)) {
				branchEnds.addOnSegment(newSegment.end(), edge);
			} else {
				fullGraph.integrateCutSegment(new SplitSegment2D(edge, newSegment.end()));
			}
		}

		fullGraph.addVertex(newSegment.end());
		fullGraph.addSegmentAsEdge(newSegment);

		NonIntersectionTest.test(fullGraph, newSegment);

		if (event.isTerminal()) {
			deadEnds.add(newSegment);
		}
	}

	private boolean edgeIsOnCycles(Segment2D edge) {
		return outerCycle.graph().containsEdge(edge)
			|| innerCycles.stream().map(OrientedCycle::graph).anyMatch(g -> g.containsEdge(edge));
	}
}