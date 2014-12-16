package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;

abstract class SkeletonEvent extends Point2D implements Comparable<SkeletonEvent> {
	final double distanceToOriginalEdge;
	/**
	 * <i>v<sub>a</sub></i> in [Obdrzalek 1998]
	 */
	// TODO: Move parent up in hierarchy
	protected final Node parent;

	SkeletonEvent(double x, double y, Node parent) {
		super(x, y);
		this.distanceToOriginalEdge = distanceToLine(parent.currentEdge);
		this.parent = parent;
	}
	@Override
	public int compareTo(SkeletonEvent o) {
		if (distanceToOriginalEdge > o.distanceToOriginalEdge) {
			return 1;
		} else if (distanceToOriginalEdge < o.distanceToOriginalEdge) {
			return -1;
		}
		return 0;
	}
}
