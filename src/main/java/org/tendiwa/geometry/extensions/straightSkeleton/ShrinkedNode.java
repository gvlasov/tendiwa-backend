package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

public class ShrinkedNode extends Node {
	ShrinkedNode(Point2D point, InitialNode previousEdgeStart, InitialNode currentEdgeStart) {
		super(point, previousEdgeStart, currentEdgeStart);
	}

	@Override
	boolean isSplitRightNode() {
		return false;
	}

	@Override
	boolean isSplitLeftNode() {
		return false;
	}
}
