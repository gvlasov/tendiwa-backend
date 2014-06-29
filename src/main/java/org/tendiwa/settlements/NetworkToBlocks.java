package org.tendiwa.settlements;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Divides space inside a network into enclosed blocks.
 */
class NetworkToBlocks {
	private final double snapSize;
	private final TestCanvas canvas;
	private final Set<NetworkWithinCycle.DirectionFromPoint> used = new HashSet<>();
	private final Set<SecondaryRoadNetworkBlock> enclosedBlocks;

	NetworkToBlocks(
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		Set<NetworkWithinCycle.DirectionFromPoint> filamentEnds,
		double snapSize,
		TestCanvas canvas
	) {
		this.snapSize = snapSize;
		this.canvas = canvas;
		if (filamentEnds.isEmpty()) {
			enclosedBlocks = ImmutableSet.of();
		} else {
			UndirectedGraph<Point2D, Segment2D> blockBoundsNetwork = copyRelevantNetwork(relevantNetwork);
			for (NetworkWithinCycle.DirectionFromPoint end : filamentEnds) {
				if (isUsed(end)) {
					continue;
				}
				edgeToClosestSnap(end, blockBoundsNetwork);
			}
			enclosedBlocks = new MinimumCycleBasis<>(blockBoundsNetwork, Point2DVertexPositionAdapter.get())
				.minimalCyclesSet()
				.stream()
				.map(cycle->new SecondaryRoadNetworkBlock(cycle.vertexList()))
				.collect(toSet());
		}
	}

	private boolean isUsed(NetworkWithinCycle.DirectionFromPoint end) {
		return used.contains(end);
	}

	public Set<SecondaryRoadNetworkBlock> getEnclosedBlocks() {
		return enclosedBlocks;
	}

	private void edgeToClosestSnap(NetworkWithinCycle.DirectionFromPoint end, UndirectedGraph<Point2D, Segment2D> blockBoundsNetwork) {
		SnapEvent test = new SnapTest(
			snapSize,
			end.node,
			end.node.moveBy(
				Math.cos(end.direction) * snapSize,
				Math.sin(end.direction) * snapSize
			),
			blockBoundsNetwork,
			canvas
		).snap();
		switch (test.eventType) {
			case NO_SNAP:
				assert false;
				break;
			case NODE_SNAP:
				blockBoundsNetwork.addEdge(end.node, test.targetNode);
				used.add(end);
				break;
			case ROAD_SNAP:
				blockBoundsNetwork.removeEdge(test.road);
				blockBoundsNetwork.addVertex(test.targetNode);
				blockBoundsNetwork.addEdge(end.node, test.targetNode);
				blockBoundsNetwork.addEdge(test.road.start, test.targetNode);
				blockBoundsNetwork.addEdge(test.road.end, test.targetNode);
				break;
			case NO_NODE:
				assert false;
				break;
			default:
				assert false;
		}
	}

	private UndirectedGraph<Point2D, Segment2D> copyRelevantNetwork(UndirectedGraph<Point2D, Segment2D> relevantNetwork) {
		UndirectedGraph<Point2D, Segment2D> blockBoundsNetwork = new SimpleGraph<>(relevantNetwork.getEdgeFactory());
		for (Point2D vertex : relevantNetwork.vertexSet()) {
			blockBoundsNetwork.addVertex(vertex);
		}
		for (Segment2D edge : relevantNetwork.edgeSet()) {
			blockBoundsNetwork.addEdge(edge.start, edge.end, edge);
		}
		return blockBoundsNetwork;
	}
}
