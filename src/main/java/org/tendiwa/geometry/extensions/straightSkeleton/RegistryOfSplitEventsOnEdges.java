package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.tendiwa.geometry.Vector2D.fromStartToEnd;

/**
 * Tracks which edges are split by which split events. Keeps all split events on an edge sorted by length of projection
 * of event-edge.start on edge.
 */
class RegistryOfSplitEventsOnEdges {
	private final Map<Node, TreeSet<SplitEventOnEdge>> edgesToSplitNodes = new HashMap<>();
	private final NodeFlowRegistry nodeFlowRegistry;

	RegistryOfSplitEventsOnEdges(List<? extends Node> nodes, NodeFlowRegistry nodeFlowRegistry) {
		this.nodeFlowRegistry = nodeFlowRegistry;
		nodes.forEach(this::initOriginalEdge);
	}

	void addSplitNode(Node oppositeEdgeStart, Node node, Orientation orientation) {
		assert edgesToSplitNodes.containsKey(oppositeEdgeStart);
		edgesToSplitNodes.get(oppositeEdgeStart).add(
			new SplitEventOnEdge(
				node,
				orientation,
				projectionOnEdge(node.vertex, oppositeEdgeStart.currentEdge)
			)
		);
	}

	Node getNodeFromRight(Node oppositeEdgeStart, Node node) {
		Node node1 = edgesToSplitNodes.get(oppositeEdgeStart).lower(
			new SplitEventOnEdge(
				node,
				Orientation.RIGHT,
				projectionOnEdge(node.vertex, oppositeEdgeStart.currentEdge)
			)
		).node;
		if (node1 == null) {
			return nodeFlowRegistry.getChainByOriginalTail(oppositeEdgeStart).getHead();
		} else {
			return node1;
		}
	}

	Node getNodeFromLeft(Node oppositeEdgeStart, Node node) {
		Node node1 = null;
		SplitEventOnEdge higher = null;
		try {
			higher = edgesToSplitNodes.get(oppositeEdgeStart).higher(
				new SplitEventOnEdge(
					node,
					Orientation.LEFT,
					projectionOnEdge(node.vertex, oppositeEdgeStart.currentEdge)
				)
			);
		}catch (NullPointerException e) {
			assert false;
		}
		if (higher != null) {
			node1 = higher.node;
		}
		if (node1 == null) {
			return nodeFlowRegistry.getChainByOriginalTail(oppositeEdgeStart).getHead();
		} else {
			return node1;
		}
	}

	private void initOriginalEdge(Node node) {
		assert node.next() != null;
		edgesToSplitNodes.put(node, new TreeSet<>(
			(o1, o2) -> {
				if (o1 == o2) {
					return 0;
				}
				assert o1.node != o2.node;
				if (o1.node.vertex.equals(o2.node.vertex)) {
					assert o1.orientation != o2.orientation;
					return o1.orientation == Orientation.LEFT ? 1 : -1;
				} else {
					assert o1.projectionLength != o2.projectionLength;
					return (int) Math.signum(o1.projectionLength - o2.projectionLength);
				}
			}
		));
	}

	private static double projectionOnEdge(Point2D vertex, Segment2D edge) {
		Vector2D edgeVector = edge.asVector();
		return fromStartToEnd(edge.start, vertex).dotProduct(edgeVector) / edgeVector.magnitude() / edgeVector.magnitude();
	}

	enum Orientation {
		LEFT, RIGHT
	}

	private class SplitEventOnEdge {
		private final Orientation orientation;
		private final Node node;
		private final double projectionLength;

		private SplitEventOnEdge(Node node, Orientation orientation, double projectionLength) {
			assert node != null;
			assert orientation != null;
			this.orientation = orientation;
			this.projectionLength = projectionLength;
			this.node = node;
		}
	}

	public static void main(String[] args) {
		Segment2D edge = Segment2D.create(4, 7, 12, 43);
		System.out.println(projectionOnEdge(edge.end, edge));
		System.out.println(projectionOnEdge(edge.start, edge));
	}
}
