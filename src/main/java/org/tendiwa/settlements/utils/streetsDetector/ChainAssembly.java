package org.tendiwa.settlements.utils.streetsDetector;

import org.tendiwa.collections.DoublyLinkedNode;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Creates chains out of joints.
 */
final class ChainAssembly {
	private final Map<Segment2D, DoublyLinkedNode<Segment2D>> bonesTo2DegreeNodes;

	public ChainAssembly(int expectedSize) {
		bonesTo2DegreeNodes = new LinkedHashMap<>(expectedSize);
	}

	private DoublyLinkedNode<Segment2D> obtainNodeForBone(Segment2D bone) {
		return bonesTo2DegreeNodes.computeIfAbsent(bone, DoublyLinkedNode::new);
	}

	void addJoint(Joint joint) {
		DoublyLinkedNode<Segment2D> node1 = obtainNodeForBone(joint.bone1);
		DoublyLinkedNode<Segment2D> node2 = obtainNodeForBone(joint.bone2);
		node1.uniteWith(node2);
	}

	boolean usedBone(Segment2D bone) {
		return bonesTo2DegreeNodes.containsKey(bone);
	}

	Collection<DoublyLinkedNode<Segment2D>> nodes() {
		return bonesTo2DegreeNodes.values();
	}
}
