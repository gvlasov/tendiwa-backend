package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

final class LeftSplitNode extends SplitNode {

	LeftSplitNode(Point2D point, InitialNode previousEdgeStart, InitialNode currentEdgeStart) {
		super(point, previousEdgeStart, currentEdgeStart);
	}

	@Override
	boolean isLeft() {
		return true;
	}

}
