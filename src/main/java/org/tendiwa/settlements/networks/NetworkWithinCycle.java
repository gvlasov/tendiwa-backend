package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.CommonEdgeSplitter;
import org.tendiwa.graphs.MinimalCycle;

import java.util.*;
import java.util.stream.Collectors;

/**
 * [Kelly section 4.3.1]
 * <p>
 * A part of {@link Segment2DSmartMesh} bounded by a fundamental basis cycle
 * (one of those in <i>minimal cycle basis</i> from [Kelly section 4.3.1, figure 41]).
 */
public final class NetworkWithinCycle {
	private final NetworkToBlocks blockDivision;
	private final SecondaryRoadNetwork secondaryRoadNetwork;
	private final OrientedCycle enclosingCycle;
	private final Collection<OrientedCycle> enclosedCycles;

	/**
	 * @param fullGraph
	 * 	Current full graph of a {@link Segment2DSmartMesh}.
	 * @param originalMinimalCycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @param enclosedCycles
	 * 	Cycles enclosed within the {@code originalMinimalCycle}.
	 * @param random
	 * 	A seeded {@link java.util.Random} used to generate the parent {@link Segment2DSmartMesh}.
	 */

	NetworkWithinCycle(
		UndirectedGraph<Point2D, Segment2D> fullGraph,
		UndirectedGraph<Point2D, Segment2D> splitOriginalGraph,
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		Collection<MinimalCycle<Point2D, Segment2D>> enclosedCycles,
		CommonEdgeSplitter<Point2D, Segment2D> commonEdgeSplitter,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {

		this.enclosingCycle = new OrientedCycle(originalMinimalCycle, splitOriginalGraph);
		this.enclosedCycles = enclosedCycles.stream()
			.map(cycle -> new OrientedCycle(cycle, splitOriginalGraph))
			.collect(Collectors.toCollection(LinkedHashSet::new));
		this.secondaryRoadNetwork = new SecondaryRoadNetwork(
			fullGraph,
			enclosingCycle,
			this.enclosedCycles,
			commonEdgeSplitter,
			networkGenerationParameters,
			random
		);
		this.blockDivision = new NetworkToBlocks(
			fullGraph,
			secondaryRoadNetwork.filamentEnds,
			networkGenerationParameters
		);
	}


	/**
	 * Creates an unmodifiable view of {@link SecondaryRoadNetwork#graph}.
	 *
	 * @return An unmodifiable graph containing this NetworkWithinCycle's secondary road network.
	 */
	public UndirectedGraph<Point2D, Segment2D> network() {
		return new UnmodifiableUndirectedGraph<>(secondaryRoadNetwork.getGraph());
	}

	public UndirectedGraph<Point2D, Segment2D> cycle() {
		return enclosingCycle.graph();
	}


	public List<SecondaryRoadNetworkBlock> enclosedBlocks() {
		return blockDivision.getEnclosedBlocks();
	}

	public ImmutableSet<Point2D> filamentEnds() {
		return secondaryRoadNetwork.filamentEndPoints;
	}

}
