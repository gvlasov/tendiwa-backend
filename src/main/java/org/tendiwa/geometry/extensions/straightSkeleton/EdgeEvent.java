package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

public class EdgeEvent extends SkeletonEvent {
	/**
	 * <i>v<sub>b</sub></i> in [Obdrzalek 1998]
	 * <p>
	 * {@code rightParent == null} means it is a split event, otherwise it is an edge event
	 */
	private final Node rightParent;
	private final Node leftParent;

	EdgeEvent(Point2D point, Node leftParent, Node rightParent) {
		super(point, leftParent);
		this.leftParent = leftParent;
		this.rightParent = rightParent;
	}

	public Node rightParent() {
		assert rightParent != null;
		return rightParent;
	}

	Node leftParent() {
		return leftParent;
	}
	@Override
	void handle(SuseikaStraightSkeleton skeleton) {
		// Convex 2b
		boolean leftProcessed = leftParent().isProcessed();
		boolean rightProcessed = rightParent().isProcessed();
		if (leftProcessed || rightProcessed) {
			if (!(leftProcessed && rightProcessed)) {
				Node node = leftProcessed ? rightParent() : leftParent();
				skeleton.queueEventFromNode(node);
			}
			return;
		}
		if (leftParent().next() != rightParent()) {
			assert false;
		}
		// Convex 2c
		if (leftParent().previous().previous() == rightParent()) {
			skeleton.connectLast3SegmentsOfLav(this);
			return;
		}
		if (leftParent().isInLavOf2Nodes()) {
			skeleton.eliminate2NodeLav(leftParent(), rightParent());
			return;
		}
		// Convex 2d
		skeleton.outputArc(leftParent().vertex, point);
		skeleton.outputArc(rightParent().vertex, point);
		skeleton.debug.drawEdgeEventArcs(this);

		// Convex 2e
		Node node = new ShrinkedNode(
			point,
			leftParent().previousEdgeStart,
			rightParent().currentEdgeStart
		);

		leftParent().growAdjacentFaces(node);
		rightParent().growAdjacentFaces(node);

		node.setPreviousInLav(leftParent().previous());
		rightParent().next().setPreviousInLav(node);
		node.computeReflexAndBisector();

		leftParent().setProcessed();
		rightParent().setProcessed();

		// Convex 2f
		skeleton.queueEventFromNode(node);
	}
}
