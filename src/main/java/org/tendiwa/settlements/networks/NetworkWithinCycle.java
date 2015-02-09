package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.CommonEdgeSplitter;
import org.tendiwa.graphs.MinimalCycle;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

/**
 * [Kelly section 4.3.1]
 * <p>
 * A part of {@link SegmentNetwork} bounded by a fundamental basis cycle
 * (one of those in <i>minimal cycle basis</i> from [Kelly section 4.3.1, figure 41]).
 */
public final class NetworkWithinCycle {
	private final NetworkToBlocks blockDivision;
	private final SecondaryRoadNetwork secondaryRoadNetwork;
	private UndirectedGraph<Point2D, Segment2D> cycleGraph;

	/**
	 * Returns a set points where secondary road network is connected with the cycle.
	 *
	 * @return A set points where secondary road network is connected with the cycle.
	 */
	public ImmutableSet<Point2D> exitsOnCycles() {
		return secondaryRoadNetwork.exitsOnCycles;
	}

	/**
	 * @param relevantNetwork
	 * 	A preconstructed graph of low level roads, constructed by
	 * 	{@link org.tendiwa.settlements.networks.NetworksProducer#constructNetworkOriginalGraph(org.tendiwa.graphs.MinimalCycle,
	 *    java.util.Set, java.util.Collection)}
	 * @param originalMinimalCycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @param originalRoadGraph
	 * 	Graph that is bounding all cells.
	 * @param filamentEdges
	 * 	A collection of all the edges of a {@link org.tendiwa.settlements.networks.SegmentNetworkBuilder#graph}
	 * 	that are not part of any minimal cycles. The same collection is passed to all the CityCells.
	 * @param enclosedCycles
	 * 	Cycles enclosed within the {@code originalMinimalCycle}.
	 * @param random
	 * 	A seeded {@link java.util.Random} used to generate the parent {@link SegmentNetwork}.
	 * @param holderOfSplitCycleEdges
	 */

	NetworkWithinCycle(
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		UndirectedGraph<Point2D, Segment2D> originalRoadGraph,
		Collection<Segment2D> filamentEdges,
		Collection<MinimalCycle<Point2D, Segment2D>> enclosedCycles,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges,
		CommonEdgeSplitter<Point2D, Segment2D> commonEdgeSplitter,
		NetworkGenerationParameters networkGeneratoinParameters,
		Random random
	) {
		this.cycleGraph = initCycleGraph(relevantNetwork);

		this.secondaryRoadNetwork = new SecondaryRoadNetwork(
			relevantNetwork,
			originalRoadGraph,
			cycleGraph,
			originalMinimalCycle,
			holderOfSplitCycleEdges,
			filamentEdges,
			enclosedCycles,
			networkGeneratoinParameters,
			random
		);
		this.blockDivision = new NetworkToBlocks(
			relevantNetwork,
			secondaryRoadNetwork.filamentEnds,
			networkGeneratoinParameters,
			holderOfSplitCycleEdges
		);
	}

	private UndirectedGraph<Point2D, Segment2D> initCycleGraph(UndirectedGraph<Point2D, Segment2D> relevantNetwork) {
		// Initially relevant network consists only of the cycle
		return new UndirectedSubgraph<>(
			relevantNetwork,
			new LinkedHashSet<>(relevantNetwork.vertexSet()),
			new LinkedHashSet<>(relevantNetwork.edgeSet())
		);
	}


	/**
	 * Creates an unmodifiable view of {@link org.tendiwa.settlements.networks.RoadInserter#secRoadNetwork}.
	 *
	 * @return An unmodifiable graph containing this NetworkWithinCycle's secondary road network.
	 */
	public UndirectedGraph<Point2D, Segment2D> network() {
		return new UnmodifiableUndirectedGraph<>(secondaryRoadNetwork.getSecondaryRoadGraph());
	}

	public UndirectedGraph<Point2D, Segment2D> cycle() {
		return cycleGraph;
	}


	public List<SecondaryRoadNetworkBlock> enclosedBlocks() {
		return blockDivision.getEnclosedBlocks();
	}

	public ImmutableSet<Point2D> filamentEnds() {
		return secondaryRoadNetwork.filamentEndPoints;
	}

}
