package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Iterators;
import com.sun.istack.internal.NotNull;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.Iterator;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 */
public class IntersectionPoint extends Point2D implements Comparable<IntersectionPoint> {
	private final double distanceToOriginalEdge;
	final EventType event;
	Node oppositeEdgeStart;
	private final TestCanvas canvas;
	final Node va;
	final Node vb;
	static Iterator<Color> colors = Iterators.cycle(Color.orange, Color.blue, Color.magenta, Color.black);

	IntersectionPoint(
		double x,
		double y,
		Node originalEdgeStart,
		Node va,
		Node vb,
		EventType event,
		TestCanvas canvas
	) {
		super(x, y);
		oppositeEdgeStart = originalEdgeStart;
		this.canvas = canvas;
		originalEdgeStart.addObserver(this);
		this.va = va;
		this.vb = vb;
		this.event = event;
		this.distanceToOriginalEdge = distanceToLine(originalEdgeStart.currentEdge);
	}

	void changeOppositeEdgeStart(Node from, Node to) {
		assert to != null;
		assert oppositeEdgeStart == from;
		assert !to.isProcessed();
		oppositeEdgeStart = to;
		from.removeObserver(this);
		to.addObserver(this);
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
