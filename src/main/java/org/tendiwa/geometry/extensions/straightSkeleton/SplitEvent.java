package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 */
public class SplitEvent extends SkeletonEvent implements Comparable<SkeletonEvent> {
	private final OriginalEdgeStart oppositeEdgeStart;
	private final Node parent;

	SplitEvent(
		Point2D point,
		Node parent,
		OriginalEdgeStart oppositeEdgeStart
	) {
		super(point, parent);
		this.parent = parent;
		this.oppositeEdgeStart = oppositeEdgeStart;
	}

	Node parent() {
		return parent;
	}

	OriginalEdgeStart oppositeEdgeStart() {
		return oppositeEdgeStart;
	}

	@Override
	void handle(SuseikaStraightSkeleton skeleton) {
		assert parent() instanceof OriginalEdgeStart;
		if (parent().isProcessed()) {
			return;
		}
		// Non-convex 2c
		if (parent().previous().previous().previous() == parent()) {
//			connectLast3SegmentsOfLav(point);
			assert false;
			return;
		}
		if (parent().next().next() == parent()) {
			skeleton.eliminate2NodeLav(parent(), parent().next().next());
			return;
		}
		// Non-convex 2D
		skeleton.outputArc(parent().vertex, point);
		skeleton.debug.drawSplitEventArc(this);
		// Non-convex 2e

		// Split event produces two nodes at the same point, and those two nodes have distinct LAVs.
		LeftSplitNode leftNode = new LeftSplitNode(
			point,
			parent().previousEdgeStart,
			oppositeEdgeStart()
		);
		RightSplitNode rightNode = new RightSplitNode(
			point,
			oppositeEdgeStart(),
			parent().currentEdgeStart
		);
		leftNode.setPair(rightNode);
		rightNode.setPair(leftNode);

		Node leftLavNextNode = oppositeEdgeStart().face().getNodeFromLeft(leftNode);
		Node rightLavPreviousNode = oppositeEdgeStart().face().getNodeFromRight(rightNode);

		leftNode.setPreviousInLav(parent().previous());
		leftLavNextNode.setPreviousInLav(leftNode);

		rightNode.setPreviousInLav(rightLavPreviousNode);
		parent().next().setPreviousInLav(rightNode);

		parent().setProcessed();


		parent().growRightFace(rightNode);
		parent().growLeftFace(leftNode);
		oppositeEdgeStart().face().addLink(leftNode, rightNode);

		// Non-convex 2
		integrateNewSplitNode(leftNode, skeleton);
		integrateNewSplitNode(rightNode, skeleton);
	}

	private void integrateNewSplitNode(Node node, SuseikaStraightSkeleton skeleton) {
		if (node.isInLavOf2Nodes()) {
			// Such lavs can form after a split event
			skeleton.eliminate2NodeLav(node, node.next());
		} else {
			node.computeReflexAndBisector();
			skeleton.queueEventFromNode(node);
		}
	}
}
