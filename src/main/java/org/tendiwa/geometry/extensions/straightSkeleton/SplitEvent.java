package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.DrawableInto;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 */
public class SplitEvent extends SkeletonEvent implements Comparable<SkeletonEvent> {
	static DrawableInto canvas;
	private final InitialNode oppositeEdgeStart;

	SplitEvent(
		double x,
		double y,
		Node parent,
		InitialNode oppositeEdgeStart
	) {
		// TODO: Make SkeletonEvent not extend Point2D
		super(x, y, parent);
		this.oppositeEdgeStart = oppositeEdgeStart;
	}

	public Node parent() {
		return parent;
	}



	public Node getOppositeEdgeStartMovementHead() {
		return oppositeEdgeStart.face.startHalfface.getLast();
	}

	public Node getOppositeEdgeEndMovementHead() {
		return oppositeEdgeStart.face.endHalfface.getLast();
	}

	public Node oppositeEdgeStart() {
		return oppositeEdgeStart;
	}
}
