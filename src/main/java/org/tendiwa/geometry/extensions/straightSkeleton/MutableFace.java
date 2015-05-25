package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.StraightSkeletonFace;

interface MutableFace extends Iterable<Node> {
	void addLink(Node one, Node another);

	boolean isClosed();

	StraightSkeletonFace toPolygon();

	Node getNodeFromLeft(LeftSplitNode leftNode);

	Node getNodeFromRight(RightSplitNode rightNode);
}
