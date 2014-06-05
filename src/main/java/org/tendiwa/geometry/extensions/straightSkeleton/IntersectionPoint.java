package org.tendiwa.geometry.extensions.straightSkeleton;

import com.sun.istack.internal.NotNull;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 */
public class IntersectionPoint extends Point2D implements Comparable<IntersectionPoint> {
	private final double distanceToOriginalEdge;
	final EventType event;
	final Node oppositeEdgeStart;
	final Node va;
	final Node vb;

	IntersectionPoint(double x, double y, Node originalEdgeStart, Node va, Node vb, EventType event) {
		super(x, y);
		oppositeEdgeStart = originalEdgeStart;
		this.va = va;
		this.vb = vb;
		this.event = event;
		this.distanceToOriginalEdge = distanceToLine(originalEdgeStart.currentEdge);
	}

	@Override
	public int compareTo(@NotNull IntersectionPoint o) {
		if (distanceToOriginalEdge > o.distanceToOriginalEdge) {
			return 1;
		} else if (distanceToOriginalEdge < o.distanceToOriginalEdge) {
			return -1;
		}
		return 0;
	}
}
