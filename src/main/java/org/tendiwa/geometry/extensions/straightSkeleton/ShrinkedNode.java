package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

public class ShrinkedNode extends Node {
	ShrinkedNode(Point2D point, InitialNode previousEdgeStart, InitialNode currentEdgeStart) {
		super(point, previousEdgeStart, currentEdgeStart);
	}

	@Override
	boolean hasPair() {
		return false;
	}

	@Override
	SplitNode getPair() {
		throw new RuntimeException("CenterNode can't have a pair; only SplitNode can");
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
