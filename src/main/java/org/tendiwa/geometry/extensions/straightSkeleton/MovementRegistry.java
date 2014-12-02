package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Segment2D;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class MovementRegistry {
	private Map<Segment2D, NodeMovement> movements = new HashMap<>();

	MovementRegistry(List<Node> nodes) {
		for (Node node : nodes) {
			movements.put(node.currentEdge, new NodeMovement(node));
		}
	}

	public NodeMovement getByOriginalEdge(Segment2D edge) {
		return movements.get(edge);
	}
	private class NodeMovementStep {
		private final LinkedList<NodeMovement> leaves = new LinkedList<>();
		void addLeaf(NodeMovement leaf) {
			leaves.add(leaf);
		}
		void updateLeaves(Node newHead) {
			leaves.forEach(leaf->leaf.moveTo(newHead));
		}
		void combineSteps(NodeMovementStep source, NodeMovementStep destination) {
			destination.leaves.addAll(source.leaves);
		}
	}
}
