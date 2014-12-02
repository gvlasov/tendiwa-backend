package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.Iterator;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 */
public class SkeletonEvent extends Point2D implements Comparable<SkeletonEvent> {
	final double distanceToOriginalEdge;
	final EventType event;
	final NodeMovement oppositeEdgeStartMovement;
	final NodeMovement oppositeEdgeEndMovement;
	Node oppositeEdgeStart;
	Node oppositeEdgeEnd;
	private final DrawableInto canvas;
	/**
	 * When {@link #event} is {@link org.tendiwa.geometry.extensions.straightSkeleton.EventType#EDGE}, {@link #va}
	 * and {@link #vb} are, accordingly, left and right predecessors of this SkeletonEvent.
	 * <p>
	 * When {@link #event} is {@link org.tendiwa.geometry.extensions.straightSkeleton.EventType#SPLIT}, {@link #va}
	 * is the predecessor and {@link #vb} is null.
	 */
	final Node va;
	final Node vb;
	static Iterator<Color> colors = Iterators.cycle(Color.orange, Color.blue, Color.magenta, Color.black);

	SkeletonEvent(
		double x,
		double y,
		NodeMovement oppositeEdgeStartMovement,
		NodeMovement oppositeEdgeEndMovement,
		Node va,
		Node vb,
		EventType event,
		DrawableInto canvas
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
//		assert oppositeEdgeStartMovement.getTail() == oppositeEdgeStartMovement.getHead();
		this.distanceToOriginalEdge = distanceToLine(oppositeEdgeStartMovement.getTail().currentEdge);
		oppositeEdgeStart = this.oppositeEdgeStartMovement.getHead();
		oppositeEdgeEnd = this.oppositeEdgeEndMovement.getHead();
	}

	void changeOppositeEdgeStart(Node start, Node to) {
		assert oppositeEdgeStartMovement.getTail() == start;
		oppositeEdgeStart = changeOppositeEdgeNode(to, oppositeEdgeStart);
	}

	public void changeOppositeEdgeEnd(Node start, Node to) {
		assert oppositeEdgeEndMovement.getTail() == start;
		oppositeEdgeEnd = changeOppositeEdgeNode(to, oppositeEdgeEnd);
	}

	private Node changeOppositeEdgeNode(Node to, Node currentNode) {
		assert to != null;
		assert !to.isProcessed() : to.vertex;
//		if (!currentNode.vertex.equals(to.vertex)) {
//			canvas.draw(
//				new Segment2D(
//					currentNode.vertex,
//					to.vertex
//				), DrawingSegment2D.withColorDirected(colors.next())
//			);
//		}
		ImmutableList<Node> nodes = ImmutableList.copyOf(to);
		if (event == EventType.EDGE) {
			// TODO: Replace with Iterators.contains
			if (nodes.contains(va) /*|| nodes.contains(vb)*/) {
				return to;
			}
		} else if (event == EventType.SPLIT) {
			// TODO: Replace with Iterators.contains
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
