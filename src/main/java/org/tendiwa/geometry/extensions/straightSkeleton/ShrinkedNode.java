package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

public class ShrinkedNode extends Node {
	ShrinkedNode(Point2D point, OriginalEdgeStart previousEdgeStart, OriginalEdgeStart currentEdgeStart) {
		super(point, previousEdgeStart, currentEdgeStart);
	}

	@Override
	boolean hasPair() {
		return false;
	}
}
