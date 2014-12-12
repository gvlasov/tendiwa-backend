package org.tendiwa.geometry.extensions.straightSkeleton;

public class EdgeEvent extends SkeletonEvent {
	/**
	 * <i>v<sub>b</sub></i> in [Obdrzalek 1998]
	 * <p>
	 * {@code rightParent == null} means it is a split event, otherwise it is an edge event
	 */
	private final Node rightParent;

	EdgeEvent(double x, double y, Node leftParent, Node rightParent) {
		super(x, y, leftParent);
		this.rightParent = rightParent;
	}

	public Node rightParent() {
		assert rightParent != null;
		return rightParent;
	}

	Node leftParent() {
		return parent;
	}
}
