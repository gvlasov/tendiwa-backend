package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chain of parent {@link Node}s. Chain has a tail and a head, and head can be changes. Observers of the
 * chain are notified when chain's head is changed.
 */
final class NodeFlow {
	/**
	 * First node in the chain.
	 * <p>
	 * Used only in asserts, may be removed when the whole algorithm works properly.
	 */
	// TODO: Remove tail node.
	private final Node tail;
	/**
	 * Last node in the chain.
	 */
	private Node head;
	private List<SkeletonEvent> startObservers = new ArrayList<>(1);
	static DrawableInto canvas;
	private List<SkeletonEvent> endObservers = new ArrayList<>(1);

	NodeFlow(Node start) {
		assert start != null;
		this.tail = start;
		this.head = start;
	}

	/**
	 * Moves chain's head to {@code newHead}
	 *
	 * @param newHead
	 * 	New chain head.
	 */
	void changeHead(Node newHead) {
		if (head.vertex.distanceTo(newHead.vertex) > 0) {
			drawMovement(newHead);
		}

		assert newHead != null;
		assert newHead != tail;
		if (newHead.isProcessed()) {
			assert false;
		}
		this.head = newHead;

		for (SkeletonEvent observer : startObservers) {
			if (newHead.isInTheSameLav(observer.leftParent())) {
				observer.setOppositeEdgeStartMovementHead(newHead);
			}
			// TODO: Remove observer if it is found that a node moved to another lav?
		}
		for (SkeletonEvent observer : endObservers) {
			if (newHead.isInTheSameLav(observer.leftParent())) {
				observer.setOppositeEdgeEndMovementHead(newHead);
			}
		}
	}

	private void drawMovement(Node newEnd) {
		canvas.draw(
			new Segment2D(
				head.vertex,
				newEnd.vertex
			),
			DrawingSegment2D.withColorDirected(Color.blue, 1)
		);
	}

	public Node getTail() {
		return tail;
	}

	public Node getHead() {
		return head;
	}


	public void addStartObserver(SkeletonEvent skeletonEvent) {
		startObservers.add(skeletonEvent);
	}

	public void addEndObserver(SkeletonEvent skeletonEvent) {
		endObservers.add(skeletonEvent);
	}

//	/**
//	 * Notifies previously computed split events of change in their start points.
//	 *
//	 * @param nextNode
//	 */
//	public void notifyObservers(Node nextNode) {
//		notifyObservers(nextNode, ImmutableList.copyOf(observers));
//	}
//
//	public void notifyObservers(Node nextNode, List<SkeletonEvent> observers) {
//		assert nextNode != null;
//		assert head != nextNode;
//		ImmutableList<Node> lav = ImmutableList.copyOf(nextNode); // NextNode works as Iterable<Node> here.
//		for (SkeletonEvent intersection : observers) {
//			TODO: Do we have to copy observers?
//			We have to copy observers because otherwise that list
//			will be concurrently modified inside this loop.
//			if (lav.contains(intersection.leftParent)) {
//				intersection.changeOppositeEdgeStart(head, nextNode);
//			}
//		}
//	}
//
//	public List<SkeletonEvent> copyObservers() {
//		return ImmutableList.copyOf(observers);
//	}

	public void removeObserver(SkeletonEvent observer) {
		assert startObservers.contains(observer);
		startObservers.remove(observer);
	}
}
