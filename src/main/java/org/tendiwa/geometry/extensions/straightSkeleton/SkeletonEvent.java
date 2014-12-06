package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.Iterator;

/**
 * Note: this class has natural ordering that is inconsistent with {@link Object#equals(Object)}.
 * <p>
 * {@link org.tendiwa.geometry.extensions.straightSkeleton.SkeletonEvent}s watch for
 * {@link NodeFlow}s and update their {@link #oppositeEdgeStartMovementHead}
 * and {@link #oppositeEdgeEndMovementHead} on each head move if
 */
public class SkeletonEvent extends Point2D implements Comparable<SkeletonEvent> {
	final double distanceToOriginalEdge;
	static DrawableInto canvas;
	/**
	 * <i>v<sub>a</sub></i> in [Obdrzalek 1998]
	 */
	final Node leftParent;
	/**
	 * <i>v<sub>b</sub></i> in [Obdrzalek 1998]
	 * <p>
	 * {@code rightParent == null} means it is a split event, otherwise it is an edge event
	 */
	final Node rightParent;
	static Iterator<Color> colors = Iterators.cycle(Color.orange, Color.blue, Color.magenta, Color.black);
	private Node oppositeEdgeStartMovementHead;
	private Node oppositeEdgeEndMovementHead;
	public NodeFlow oppositeEdgeStartMovement;

	SkeletonEvent(
		double x,
		double y,
		NodeFlow oppositeEdgeStartMovement,
		NodeFlow oppositeEdgeEndMovement,
		Node leftParent,
		Node rightParent
	) {
		// TODO: Make SkeletonEvent not extend Point2D
		super(x, y);
		assert oppositeEdgeStartMovement != null;
		assert oppositeEdgeEndMovement != null;
		assert leftParent != null;
		this.oppositeEdgeStartMovement = oppositeEdgeStartMovement;
//		this.oppositeEdgeEndMovement.addEndObserver(this);
		oppositeEdgeStartMovementHead = oppositeEdgeStartMovement.getHead();
		oppositeEdgeEndMovementHead = oppositeEdgeEndMovement.getHead();
		this.distanceToOriginalEdge = distanceToLine(leftParent.currentEdge);
		this.leftParent = leftParent;
		this.rightParent = rightParent;
//		assert oppositeEdgeStartMovement.getTail() == oppositeEdgeStartMovement.getHead();
	}

	boolean isSplitEvent() {
		return rightParent == null;
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
		if (isSplitEvent()) {
			// TODO: Replace with Iterators.contains
			if (nodes.contains(leftParent)) {
				return to;
			}
		} else {
			// TODO: Replace with Iterators.contains
			if (nodes.contains(leftParent) /*|| nodes.contains(rightParent)*/) {
				return to;
			}
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

	void setOppositeEdgeStartMovementHead(Node oppositeEdgeStartMovementHead) {
		this.oppositeEdgeStartMovementHead = oppositeEdgeStartMovementHead;
	}

	void setOppositeEdgeEndMovementHead(Node oppositeEdgeEndMovementHead) {
		this.oppositeEdgeEndMovementHead = oppositeEdgeEndMovementHead;
	}

	public Node getOppositeEdgeStartMovementHead() {
		return oppositeEdgeStartMovementHead;
	}

	public Node getOppositeEdgeEndMovementHead() {
		return oppositeEdgeEndMovementHead;
	}
}
