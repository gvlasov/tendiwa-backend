package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.Iterator;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 */
public class SkeletonEvent extends Point2D implements Comparable<SkeletonEvent> {
	 final double distanceToOriginalEdge;
	final EventType event;
	final OppositeEdgeStartMovement oppositeEdgeStartMovement;
	final OppositeEdgeStartMovement oppositeEdgeEndMovement;
	Node oppositeEdgeStart;
	Node oppositeEdgeEnd;
	private final TestCanvas canvas;
	final Node va;
	final Node vb;
	static Iterator<Color> colors = Iterators.cycle(Color.orange, Color.blue, Color.magenta, Color.black);

	SkeletonEvent(
		double x,
		double y,
		OppositeEdgeStartMovement oppositeEdgeStartMovement,
		OppositeEdgeStartMovement oppositeEdgeEndMovement,
		Node va,
		Node vb,
		EventType event,
		TestCanvas canvas
	) {
		super(x, y);
		this.oppositeEdgeStartMovement = oppositeEdgeStartMovement;
		this.oppositeEdgeEndMovement = oppositeEdgeEndMovement;
		this.canvas = canvas;
		this.oppositeEdgeStartMovement.addStartObserver(this);
		this.oppositeEdgeEndMovement.addEndObserver(this);
		this.va = va;
		this.vb = vb;
		this.event = event;
//		assert oppositeEdgeStartMovement.getStart() == oppositeEdgeStartMovement.getEnd();
		this.distanceToOriginalEdge = distanceToLine(oppositeEdgeStartMovement.getStart().currentEdge);
		oppositeEdgeStart = this.oppositeEdgeStartMovement.getEnd();
		oppositeEdgeEnd = this.oppositeEdgeEndMovement.getEnd();
	}

	void changeOppositeEdgeStart(Node start, Node to) {
		assert oppositeEdgeStartMovement.getStart() == start;
		oppositeEdgeStart = changeOppositeEdgeNode(to, oppositeEdgeStart);
	}

	public void changeOppositeEdgeEnd(Node start, Node to) {
		assert oppositeEdgeEndMovement.getStart() == start;
		oppositeEdgeEnd = changeOppositeEdgeNode(to, oppositeEdgeEnd);
	}

	private Node changeOppositeEdgeNode(Node to, Node currentNode) {
		assert to != null;
		assert !to.isProcessed();
//		canvas.draw(
//			new Segment2D(
//				from.vertex,
//				to.vertex
//			), DrawingSegment2D.withColorDirected(colors.next())
//		);
		ImmutableList<Node> nodes = ImmutableList.copyOf(to);
		if (event == EventType.EDGE) {
			if (nodes.contains(va) /*|| nodes.contains(vb)*/) {
				return to;
			}
		} else if (event == EventType.SPLIT) {
			if (nodes.contains(va)) {
				return to;
			}
		} else {
			throw new RuntimeException("Wrong event type");
		}
		return currentNode;
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
