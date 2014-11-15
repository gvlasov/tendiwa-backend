package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Divides space inside a network into enclosed blocks.
 */
class NetworkToBlocks {
	private final Set<SecondaryRoadNetworkBlock> enclosedBlocks;

	NetworkToBlocks(
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		Set<DirectionFromPoint> filamentEnds,
		double snapSize,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges) {
		if (!filamentEnds.isEmpty()) {
			relevantNetwork = PlanarGraphs.copyRelevantNetwork(relevantNetwork);
			Set<Segment2D> edgesCopy = ImmutableSet.copyOf(relevantNetwork.edgeSet());
			for (Segment2D edge : edgesCopy) {
				if (holderOfSplitCycleEdges.isEdgeSplit(edge)) {
					relevantNetwork.removeEdge(edge);
					UndirectedGraph<Point2D, Segment2D> graph = holderOfSplitCycleEdges.getGraph(edge);
					for (Point2D vertex : graph.vertexSet()) {
						relevantNetwork.addVertex(vertex);
					}
					for (Segment2D splitEdge : graph.edgeSet()) {
						relevantNetwork.addEdge(splitEdge.start, splitEdge.end, splitEdge);
					}
				}
			}

			assert areAllEdgesOfDegree1(filamentEnds, relevantNetwork);
			GraphLooseEndsCloser
				.withSnapSize(snapSize)
				.withFilamentEnds(filamentEnds)
				.mutateGraph(relevantNetwork);
		}
		enclosedBlocks = new MinimumCycleBasis<>(relevantNetwork, Point2DVertexPositionAdapter.get())
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new SecondaryRoadNetworkBlock(cycle.vertexList()))
			.collect(toSet());
	}

	private boolean areAllEdgesOfDegree1(Set<DirectionFromPoint> filamentEnds, UndirectedGraph<Point2D, Segment2D> r) {
		return filamentEnds.stream().allMatch(e -> r.degreeOf(e.node) == 1);
	}

	public Set<SecondaryRoadNetworkBlock> getEnclosedBlocks() {
		return enclosedBlocks;
	}

}
