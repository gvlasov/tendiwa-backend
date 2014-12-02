package org.tendiwa.geometry.extensions.straightSkeleton;

import java.util.LinkedList;

class NodeMovementStep {
	private final LinkedList<Node> leaves = new LinkedList<>();
	void addLeaf(Node leaf) {
		leaves.add(leaf);
	}
}

