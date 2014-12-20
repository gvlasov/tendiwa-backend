package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

abstract class SplitNode extends Node {
	/**
	 * Node at the same {@link org.tendiwa.geometry.Point2D} created as a result of {@link org.tendiwa.geometry
	 * .extensions.straightSkeleton.SplitEvent}. Its {@link #pair} points to this node.
	 * <p>
	 * This field is not final just because nodes have to be added mutually, and for that both nodes must already be
	 * constructed.
	 */
	private SplitNode pair;

	SplitNode(Point2D point, OriginalEdgeStart previousEdgeStart, OriginalEdgeStart currentEdgeStart) {
		super(point, previousEdgeStart, currentEdgeStart);
	}

	void setPair(SplitNode pair) {
		assert pair.vertex.equals(vertex);
		this.pair = pair;
	}


	@Override
	boolean hasPair() {
		return true;
	}

	@Override
	SplitNode getPair() {
		return pair;
	}


	@Override
	public boolean isPair(Node node) {
		return pair == node;
	}

	abstract boolean isLeft();
}
