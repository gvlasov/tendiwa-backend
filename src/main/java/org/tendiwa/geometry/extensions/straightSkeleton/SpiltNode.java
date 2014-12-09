package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

public class SpiltNode extends Node {
	private final boolean left;

	SpiltNode(Point2D point, InitialNode previousEdgeStart, InitialNode currentEdgeStart, boolean left) {
		super(point, previousEdgeStart, currentEdgeStart);
		this.left = left;
	}

	@Override
	boolean isSplitRightNode() {
		return !left;
	}

	@Override
	boolean isSplitLeftNode() {
		return left;
	}
}
