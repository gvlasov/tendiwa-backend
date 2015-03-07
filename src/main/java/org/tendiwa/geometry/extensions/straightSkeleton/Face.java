package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Polygon;

interface Face extends Iterable<Node> {
	void addLink(Node one, Node another);

	boolean isClosed();

	Polygon toPolygon();

	Node getNodeFromLeft(LeftSplitNode leftNode);

	Node getNodeFromRight(RightSplitNode rightNode);
}
