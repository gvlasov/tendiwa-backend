package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.sun.istack.internal.NotNull;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.Iterator;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 */
public class IntersectionPoint extends Point2D implements Comparable<IntersectionPoint> {
	private final double distanceToOriginalEdge;
	final EventType event;
	OppositeEdgeStartMovement movement;
	Node opposideEdgeStart;
	private final TestCanvas canvas;
	final Node va;
	final Node vb;
	static Iterator<Color> colors = Iterators.cycle(Color.orange, Color.blue, Color.magenta, Color.black);

	IntersectionPoint(
		double x,
		double y,
		OppositeEdgeStartMovement movement,
		Node va,
		Node vb,
		EventType event,
		TestCanvas canvas
	) {
		super(x, y);
		this.movement = movement;
		this.canvas = canvas;
		this.movement.addObserver(this);
		this.va = va;
		this.vb = vb;
		this.event = event;
//		assert movement.getStart() == movement.getEnd();
		this.distanceToOriginalEdge = distanceToLine(movement.getEnd().currentEdge);
		opposideEdgeStart = this.movement.getEnd();
	}

	void changeOppositeEdgeStart(Node start, Node to) {
		assert to != null;
		assert !to.isProcessed();
		assert movement.getStart() == start;
//		canvas.draw(
//			new Segment2D(
//				from.vertex,
//				to.vertex
//			), DrawingSegment2D.withColorDirected(colors.next())
//		);
		if (ImmutableList.copyOf(to).contains(va)) {
			opposideEdgeStart = to;
		}
//		from.removeObserver(this);
//		to.addObserver(this);
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
