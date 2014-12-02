package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Node} produces a chain of {@link Node}s as edges collapse.
 * {@link org.tendiwa.geometry.extensions.straightSkeleton.NodeMovement} holds the end of such chain and remembers
 * which nodes observe {@link Node}'s movement.
 */
final class NodeMovement {
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

	NodeMovement(Node start) {
		assert start != null;
		this.tail = start;
		this.head = start;
	}

	/**
	 * Moves chain's head to {@code newEnd}
	 *
	 * @param newEnd
	 * 	New chain head.
	 */
	void moveTo(Node newEnd) {
		if (head.vertex.distanceTo(newEnd.vertex) > 0) {
			drawMovement(newEnd);
		}

		assert newEnd != null;
		assert newEnd != tail;
		if (tail.vertex.chebyshovDistanceTo(new Point2D(331, 703)) < 1.3) {
			TestCanvas.canvas.draw(
				new Segment2D(head.vertex, newEnd.vertex), DrawingSegment2D.withColorThin(Color.magenta)
			);
			System.out.println(1);
		}
		this.head = newEnd;
		for (SkeletonEvent observer : startObservers) {
			observer.changeOppositeEdgeStart(tail, newEnd);
		}
		for (SkeletonEvent observer : endObservers) {
			observer.changeOppositeEdgeEnd(tail, newEnd);
		}
	}

	private void drawMovement(Node newEnd) {
		canvas.draw(
			new Segment2D(
				head.vertex,
				newEnd.vertex
			),
			DrawingSegment2D.withColorDirected(Color.blue)
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
//			if (lav.contains(intersection.va)) {
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
