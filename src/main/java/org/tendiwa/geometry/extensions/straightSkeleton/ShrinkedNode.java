package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

final class ShrinkedNode extends Node {
	ShrinkedNode(Point2D point, OriginalEdgeStart previousEdgeStart, OriginalEdgeStart currentEdgeStart) {
		super(point, previousEdgeStart, currentEdgeStart);
	}

	@Override
	boolean hasPair() {
		return false;
	}
}
