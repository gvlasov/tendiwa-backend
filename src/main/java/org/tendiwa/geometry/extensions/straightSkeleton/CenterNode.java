package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

public class CenterNode extends Node {

	CenterNode(Point2D point) {
		super(point);
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
