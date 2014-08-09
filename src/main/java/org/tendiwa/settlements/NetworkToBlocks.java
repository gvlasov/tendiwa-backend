package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.GraphExplorer;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Divides space inside a network into enclosed blocks.
 */
public class NetworkToBlocks {
	private final Set<SecondaryRoadNetworkBlock> enclosedBlocks;

	NetworkToBlocks(
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		Set<DirectionFromPoint> filamentEnds,
		double snapSize
	) {
		if (!filamentEnds.isEmpty()) {
			UndirectedGraph<Point2D, Segment2D> r = relevantNetwork;
			assert filamentEnds.stream().allMatch(e -> r.degreeOf(e.node) == 1);
			relevantNetwork = PlanarGraphs.copyRelevantNetwork(relevantNetwork);
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

	public Set<SecondaryRoadNetworkBlock> getEnclosedBlocks() {
		return enclosedBlocks;
	}

}
