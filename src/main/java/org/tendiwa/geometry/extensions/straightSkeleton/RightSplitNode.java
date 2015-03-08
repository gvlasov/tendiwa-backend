package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

final class RightSplitNode extends SplitNode {
	RightSplitNode(Point2D point, OriginalEdgeStart previousEdgeStart, OriginalEdgeStart currentEdgeStart) {
		super(point, previousEdgeStart, currentEdgeStart);
	}

	@Override
	boolean isLeft() {
		return false;
	}
}
