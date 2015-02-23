package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.algorithms.SameOrPerpendicularSlopeGraphEdgesPerturbations;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * [Kelly section 4.3.1]
 * <p>
 * A part of {@link Segment2DSmartMesh} bounded by a fundamental basis cycle
 * (one of those in <i>minimal cycle basis</i> from [Kelly section 4.3.1, figure 41]).
 */
public final class NetworkWithinCycle {
	private final InnerForest innerForest;
	private final OrientedCycle enclosingCycle;

	/**
	 * @param fullNetwork
	 * 	Current full graph of a {@link Segment2DSmartMesh}.
	 * @param originalMinimalCycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @param enclosedMinimalCycles
	 * 	Cycles enclosed within the {@code originalMinimalCycle}.
	 * @param random
	 * 	A seeded {@link java.util.Random} used to generate the parent {@link Segment2DSmartMesh}.
	 */

	NetworkWithinCycle(
		FullNetwork fullNetwork,
		SplitOriginalMesh splitOriginalMesh,
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		Collection<MinimalCycle<Point2D, Segment2D>> enclosedMinimalCycles,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {

		this.enclosingCycle = splitOriginalMesh.createCycleNetworkPart(originalMinimalCycle);
		fullNetwork.addNetworkPart(enclosingCycle);

		Set<OrientedCycle> enclosedCycles = enclosedMinimalCycles.stream()
			.map(splitOriginalMesh::createCycleNetworkPart)
			.collect(Collectors.toCollection(LinkedHashSet::new));
		enclosedCycles.forEach(fullNetwork::addNetworkPart);

		this.innerForest = new InnerForest(
			fullNetwork,
			splitOriginalMesh,
			enclosingCycle,
			enclosedCycles,
			networkGenerationParameters,
			random
		);
	}

	public UndirectedGraph<Point2D, Segment2D> network() {
		return new UnmodifiableUndirectedGraph<>(innerForest.graph());
	}

	public UndirectedGraph<Point2D, Segment2D> cycle() {
		return enclosingCycle.graph();
	}


	public List<SecondaryRoadNetworkBlock> enclosedBlocks() {
		UndirectedGraph<Point2D, Segment2D> networkPartGraph = PlanarGraphs.copyGraph(innerForest.graph());
		Graphs.addGraph(networkPartGraph, cycle());
		SameOrPerpendicularSlopeGraphEdgesPerturbations.perturb(networkPartGraph, 1e-4);

		return PlanarGraphs
			.minimumCycleBasis(networkPartGraph)
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new SecondaryRoadNetworkBlock(cycle.vertexList()))
			.collect(toList());
	}

	ImmutableSet<Segment2D> innerTreesEndSegments() {
		return innerForest.leavesWithPetioles();
	}

	public Set<Point2D> exitsOnCycles() {
		return innerForest.treeRoots();
	}
}
