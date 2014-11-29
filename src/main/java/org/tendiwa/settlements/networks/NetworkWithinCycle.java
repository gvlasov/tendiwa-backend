package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

/**
 * [Kelly section 4.3.1]
 * <p>
 * A part of {@link org.tendiwa.settlements.networks.RoadsPlanarGraphModel} bounded by a fundamental basis cycle
 * (one of those in <i>minimal cycle basis</i> from [Kelly section 4.3.1, figure 41]).
 */
public final class NetworkWithinCycle {
	private final NetworkToBlocks blockDivision;
	private final SecondaryRoadNetwork secondaryRoadNetwork;
	private CycleGraph cycleGraph;

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
	 * 	{@link org.tendiwa.settlements.networks.RoadsPlanarGraphModel#constructNetworkOriginalGraph(org.tendiwa.graphs.MinimalCycle,
	 *    java.util.Set, java.util.Collection)}
	 * @param originalMinimalCycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @param originalRoadGraph
	 * 	Graph that is bounding all cells.
	 * @param filamentEdges
	 * 	A collection of all the edges of a {@link RoadsPlanarGraphModel#originalRoadGraph} that are not
	 * 	part of any minimal cycles. The same collection is passed to all the CityCells.
	 * @param roadsFromPoint
	 * 	[Kelly figure 42, variable ParamDegree]
	 * 	<p>
	 * 	How many lines would normally go from one point of secondary road network. A NetworkWithinCycle is not
	 * 	guaranteed
	 * 	to have exactly {@code maxRoadsFromPoint} starting roads, because such amount might not fit into a cell.
	 * @param roadSegmentLength
	 * 	[Kelly figure 42, variable ParamSegmentLength]
	 * 	<p>
	 * 	Mean length of secondary network roads.
	 * @param snapSize
	 * 	[Kelly figure 42, variable ParamSnapSize]
	 * 	<p>
	 * 	A radius around secondary roads' end points inside which new end points would snap to existing ones.
	 * @param connectivity
	 * 	[Kelly figure 42, variable ParamConnectivity]
	 * 	<p>
	 * 	How likely it is to snap to node or road when possible. When connectivity == 1.0, algorithm will always
	 * 	snap when possible. When connectivity == 0.0, algorithm will never snap.
	 * @param secondaryRoadNetworkDeviationAngle
	 * 	An angle in radians. How much is secondary roads' direction randomized.
	 * 	<p>
	 * 	Kelly doesn't have this as a parameter, it is implied in [Kelly figure 42] under "deviate newDirection"
	 * 	and "calculate deviated boundaryRoad perpendicular".
	 * @param secondaryRoadNetworkRoadLengthDeviation
	 * 	A length in cells. How much is secondary roads' length randomized.
	 * 	<p>
	 * 	Kelly doesn't have this as a parameter, it is implied in [Kelly figure 42] under "calculate deviated
	 * 	ParamSegmentLength".
	 * @param maxNumOfStartPoints
	 * 	Number of starting points for road generation
	 * 	<p>
	 * 	In [Kelly figure 43] there are 2 starting points.
	 * 	<p>
	 * 	A NetworkWithinCycle is not guaranteed to have exactly {@code maxNumOfStartPoints} starting roads, because such
	 * 	amount might not fit into a cell, or because it might be necessary to start extra roads for full coverage of
	 * 	the inside of the cycle.
	 * @param random
	 * 	A seeded {@link java.util.Random} used to generate the parent {@link RoadsPlanarGraphModel}.
	 * @param favourAxisAlignedSegments
	 * @param holderOfSplitCycleEdges
	 */

	NetworkWithinCycle(
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		UndirectedGraph<Point2D, Segment2D> originalRoadGraph,
		Collection<Segment2D> filamentEdges,
		int roadsFromPoint,
		double roadSegmentLength,
		double snapSize,
		double connectivity,
		double secondaryRoadNetworkDeviationAngle,
		double secondaryRoadNetworkRoadLengthDeviation,
		int maxNumOfStartPoints,
		Random random,
		boolean favourAxisAlignedSegments,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges
	) {
		this.cycleGraph = new CycleGraph(
			originalMinimalCycle,
			holderOfSplitCycleEdges
		);
		this.secondaryRoadNetwork = new SecondaryRoadNetwork(
			roadsFromPoint,
			maxNumOfStartPoints,
			roadSegmentLength,
			snapSize,
			connectivity,
			secondaryRoadNetworkRoadLengthDeviation,
			secondaryRoadNetworkDeviationAngle,
			favourAxisAlignedSegments,
			relevantNetwork,
			originalRoadGraph,
			originalMinimalCycle,
			holderOfSplitCycleEdges,
			filamentEdges,
			random
		);
		this.blockDivision = new NetworkToBlocks(
			relevantNetwork,
			secondaryRoadNetwork.filamentEnds,
			roadSegmentLength + secondaryRoadNetworkRoadLengthDeviation,
			holderOfSplitCycleEdges
		);
	}


	/**
	 * Creates an unmodifiable view of {@link org.tendiwa.settlements.networks.SecondaryRoadNetwork#secRoadNetwork}.
	 *
	 * @return An unmodifiable graph containing this NetworkWithinCycle's secondary road network.
	 */
	public UndirectedGraph<Point2D, Segment2D> network() {
		return new UnmodifiableUndirectedGraph<>(secondaryRoadNetwork.secRoadNetwork);
	}

	public UndirectedGraph<Point2D, Segment2D> cycle() {
		return cycleGraph.getGraph();
	}


	public Set<SecondaryRoadNetworkBlock> enclosedBlocks() {
		return blockDivision.getEnclosedBlocks();
	}

	public ImmutableSet<Point2D> filamentEnds() {
		return secondaryRoadNetwork.filamentEndPoints;
	}

}
