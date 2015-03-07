package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

final class EdgeEvent extends SkeletonEvent {
	/**
	 * <i>v<sub>b</sub></i> in [Obdrzalek 1998]
	 * <p>
	 * {@code rightParent == null} means it is a split event, otherwise it is an edge event
	 */
	private final Node rightParent;
	private final Node leftParent;

	EdgeEvent(Point2D point, Node leftParent, Node rightParent) {
		super(
			point,
			leftIsCloserThanRight(point, leftParent, rightParent) ? leftParent : rightParent
		);
		this.leftParent = leftParent;
		this.rightParent = rightParent;
	}

	private static boolean leftIsCloserThanRight(Point2D point, Node leftParent, Node rightParent) {
		return point.distanceToLine(leftParent.currentEdge) < point.distanceToLine(rightParent.currentEdge);
	}

	Node rightParent() {
		assert rightParent != null;
		return rightParent;
	}

	Node leftParent() {
		return leftParent;
	}

	@Override
	void handle(SuseikaStraightSkeleton skeleton) {
		if (anyOfNodesIsProcessed()) {
			// Convex 2b
			tryQueueingNewNode(skeleton);
		} else if (lavOf3NodesLeft()) {
			// Convex 2c
			connectLast3SegmentsOfLav(skeleton);
		} else if (leftParent.isInLavOf2Nodes()) {
			eliminate2NodeLav(leftParent, skeleton);
		} else {
			shrinkEdge(skeleton);
		}
	}

	private boolean anyOfNodesIsProcessed() {
		return leftParent.isProcessed() || rightParent.isProcessed();
	}

	private void tryQueueingNewNode(SuseikaStraightSkeleton skeleton) {
		boolean leftProcessed = leftParent.isProcessed();
		boolean rightProcessed = rightParent.isProcessed();
		if (!(leftProcessed && rightProcessed)) {
			Node unprocessed = leftProcessed ? rightParent : leftParent;
			skeleton.queueEventFromNode(unprocessed);
		}
	}

	private void connectLast3SegmentsOfLav(SuseikaStraightSkeleton skeleton) {
		Node centerNode = new CenterNode(point);
		skeleton.outputArc(leftParent.vertex, point);
		skeleton.outputArc(rightParent.vertex, point);
		skeleton.outputArc(leftParent.previous().vertex, point);
		skeleton.debug.draw3NodeLavArcs(this);

		leftParent.growAdjacentFaces(centerNode);
		rightParent.growAdjacentFaces(centerNode);
		leftParent.previous().growAdjacentFaces(centerNode);

		leftParent.setProcessed();
		rightParent.setProcessed();
		leftParent.previous().setProcessed();

		assert leftParent.previous() == rightParent.next();
	}

	private void shrinkEdge(SuseikaStraightSkeleton skeleton) {
		// Convex 2d
		skeleton.outputArc(leftParent.vertex, point);
		skeleton.outputArc(rightParent.vertex, point);
		skeleton.debug.drawEdgeEventArcs(leftParent, rightParent, point);

		// Convex 2e
		Node node = new ShrinkedNode(
			point,
			leftParent.previousEdgeStart,
			rightParent.currentEdgeStart
		);

		leftParent.growAdjacentFaces(node);
		rightParent.growAdjacentFaces(node);

		node.setPreviousInLav(leftParent.previous());
		rightParent.next().setPreviousInLav(node);
		node.computeReflexAndBisector();

		leftParent.setProcessed();
		rightParent.setProcessed();

		// Convex 2f
		skeleton.queueEventFromNode(node);
	}

	private boolean lavOf3NodesLeft() {
		return leftParent.previous().previous() == rightParent;
	}
}