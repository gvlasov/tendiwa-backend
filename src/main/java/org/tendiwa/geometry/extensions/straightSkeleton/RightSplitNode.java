package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

public class RightSplitNode extends SplitNode {
	RightSplitNode(Point2D point, InitialNode previousEdgeStart, InitialNode currentEdgeStart) {
		super(point, previousEdgeStart, currentEdgeStart);
	}

	@Override
	boolean isLeft() {
		return false;
	}
}
