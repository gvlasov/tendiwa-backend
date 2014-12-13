package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

public class SplitNode extends Node {
	private final boolean left;
	/**
	 * Node at the same {@link org.tendiwa.geometry.Point2D} created as a result of {@link org.tendiwa.geometry
	 * .extensions.straightSkeleton.SplitEvent}. Its {@link #pair} points to this node.
	 * <p>
	 * This field is not final just because nodes have to be added mutually, and for that both nodes must already be
	 * constructed.
	 */
	private SplitNode pair;

	SplitNode(Point2D point, InitialNode previousEdgeStart, InitialNode currentEdgeStart, boolean left) {
		super(point, previousEdgeStart, currentEdgeStart);
		this.left = left;
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
	boolean isSplitRightNode() {
		return !left;
	}

	@Override
	boolean isSplitLeftNode() {
		return left;
	}
}
