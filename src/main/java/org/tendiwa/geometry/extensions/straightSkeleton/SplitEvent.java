package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.DrawableInto;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 */
public class SplitEvent extends SkeletonEvent implements Comparable<SkeletonEvent> {
	static DrawableInto canvas;
	private final OriginalEdgeStart oppositeEdgeStart;

	SplitEvent(
		double x,
		double y,
		Node parent,
		OriginalEdgeStart oppositeEdgeStart
	) {
		// TODO: Make SkeletonEvent not extend Point2D
		super(x, y, parent);
		this.oppositeEdgeStart = oppositeEdgeStart;
	}

	Node parent() {
		return parent;
	}

	OriginalEdgeStart oppositeEdgeStart() {
		return oppositeEdgeStart;
	}
}
