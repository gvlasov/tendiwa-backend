package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.util.*;

import static org.tendiwa.geometry.Vector2D.fromStartToEnd;

/**
 * Tracks what edges are split by what split events. Keeps all split events on an edge sorted by length of projection
 * of event-edge.start on edge.
 */
class RegistryOfSplitEventsOnEdges {
	private final Map<Segment2D, TreeSet<SplitEventOnEdge>> edgesToSplitNodes = new HashMap<>();
	private final Map<Segment2D, Node> originalEdgeStarts = new HashMap<>();
	private final Map<Segment2D, Node> originalEdgeEnds = new HashMap<>();
	private final NodeFlowRegistry nodeFlowRegistry;

	RegistryOfSplitEventsOnEdges(LinkedList<Node> nodes, NodeFlowRegistry nodeFlowRegistry) {
		this.nodeFlowRegistry = nodeFlowRegistry;
		for (Node node : nodes) {
			initOriginalEdge(node.currentEdge, node);
		}
	}

	void addSplitNode(Segment2D edge, Node node, Orientation orientation) {
		assert edgesToSplitNodes.containsKey(edge);
		edgesToSplitNodes.get(edge).add(
			new SplitEventOnEdge(
				node,
				orientation,
				projectionOnEdge(node.vertex, edge)
			)
		);
	}

	Node getNodeFromRight(Segment2D edge, Node node) {
		Node node1 = edgesToSplitNodes.get(edge).lower(
			new SplitEventOnEdge(
				node,
				Orientation.RIGHT,
				projectionOnEdge(node.vertex, edge)
			)
		).node;
		if (node1 == null) {
			assert originalEdgeStarts.containsKey(edge);
			return nodeFlowRegistry.getChainByTail(originalEdgeStarts.get(edge)).getHead();
		} else {
			return node1;
		}
	}

	Node getNodeFromLeft(Segment2D edge, Node node) {
		Node node1 = null;
		SplitEventOnEdge higher = edgesToSplitNodes.get(edge).higher(
			new SplitEventOnEdge(
				node,
				Orientation.LEFT,
				projectionOnEdge(node.vertex, edge)
			)
		);
		if (higher != null) {
			node1 = higher.node;
		}
		if (node1 == null) {
			assert originalEdgeEnds.containsKey(edge);
			return nodeFlowRegistry.getChainByTail(originalEdgeEnds.get(edge)).getHead();
		} else {
			return node1;
		}
	}

	private void initOriginalEdge(Segment2D edge, Node node) {
		assert edge != null;
		assert node.next != null;
		// TODO: Remove unused tree set?
		TreeSet<SplitEventOnEdge> set = new TreeSet<>(
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
		);

		originalEdgeStarts.put(edge, node);
		originalEdgeEnds.put(edge, node.next);
		edgesToSplitNodes.put(edge, set);
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
