package org.tendiwa.geometry.extensions.straightSkeleton;

public class NodeObserver {
	private Node node;

	public NodeObserver(Node node) {
		this.node = node;
	}

	public void changeNode(Node from, Node to) {
		assert node == from;
		node = to;
	}
}
