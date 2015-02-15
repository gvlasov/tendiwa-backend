package org.tendiwa.settlements.networks;

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
	private final SecondaryRoadNetwork secondaryRoadNetwork;
	private final OrientedCycle enclosingCycle;
	private final NetworkGenerationParameters networkGenerationParameters;

	/**
	 * @param fullGraph
	 * 	Current full graph of a {@link Segment2DSmartMesh}.
	 * @param originalMinimalCycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @param enclosedMinimalCycles
	 * 	Cycles enclosed within the {@code originalMinimalCycle}.
	 * @param random
	 * 	A seeded {@link java.util.Random} used to generate the parent {@link Segment2DSmartMesh}.
	 */

	NetworkWithinCycle(
		FullNetwork fullGraph,
		UndirectedGraph<Point2D, Segment2D> splitOriginalGraph,
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		Collection<MinimalCycle<Point2D, Segment2D>> enclosedMinimalCycles,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {
		this.networkGenerationParameters = networkGenerationParameters;

		this.enclosingCycle = new OrientedCycle(originalMinimalCycle, splitOriginalGraph);

		Set<OrientedCycle> enclosedCycles = enclosedMinimalCycles.stream()
			.map(cycle -> new OrientedCycle(cycle, splitOriginalGraph))
			.collect(Collectors.toCollection(LinkedHashSet::new));
		enclosedCycles.forEach(fullGraph::addOrientedCycle);

		this.secondaryRoadNetwork = new SecondaryRoadNetwork(
			fullGraph,
			enclosingCycle,
			enclosedCycles,
			networkGenerationParameters,
			random
		);
	}


	/**
	 * Creates an unmodifiable view of {@link SecondaryRoadNetwork#graph}.
	 *
	 * @return An unmodifiable graph containing this NetworkWithinCycle's secondary road network.
	 */
	public UndirectedGraph<Point2D, Segment2D> network() {
		return new UnmodifiableUndirectedGraph<>(secondaryRoadNetwork.graph());
	}

	public UndirectedGraph<Point2D, Segment2D> cycle() {
		return enclosingCycle.graph();
	}


	public List<SecondaryRoadNetworkBlock> enclosedBlocks() {

		UndirectedGraph<Point2D, Segment2D> graphWithLooseEnds = PlanarGraphs.copyGraph(secondaryRoadNetwork.graph());
		Graphs.addGraph(graphWithLooseEnds, cycle());
		if (!secondaryRoadNetwork.filamentEnds.isEmpty()) {
			assert secondaryRoadNetwork.filamentEnds.stream().allMatch(end -> graphWithLooseEnds.degreeOf(end.node)
				== 1);
			GraphLooseEndsCloser
				.withSnapSize(
					networkGenerationParameters.segmentLength
						+ networkGenerationParameters.secondaryNetworkSegmentLengthDeviation
				)
				.withFilamentEnds(secondaryRoadNetwork.filamentEnds)
				.mutateGraph(graphWithLooseEnds);
		}
		SameOrPerpendicularSlopeGraphEdgesPerturbations.perturb(graphWithLooseEnds, 1e-4);

		return PlanarGraphs
			.minimumCycleBasis(graphWithLooseEnds)
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new SecondaryRoadNetworkBlock(cycle.vertexList()))
			.collect(toList());
	}

	public ImmutableSet<Point2D> filamentEnds() {
		return secondaryRoadNetwork.filamentEndPoints;
	}

}
