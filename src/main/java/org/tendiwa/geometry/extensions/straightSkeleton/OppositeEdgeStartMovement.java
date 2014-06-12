package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This class tracks the movement of a start point of a segment that sweeps a polygon's face.
 */
public class OppositeEdgeStartMovement {
	private Node start;
	private Node end;
	private List<SkeletonEvent> startObservers = new ArrayList<>(1);
	static TestCanvas canvas;
	private List<SkeletonEvent> endObservers = new ArrayList<>(1);

	public OppositeEdgeStartMovement(Node start) {
		assert start != null;
		this.start = start;
		this.end = start;
	}

	public void moveTo(Node newEnd) {
//		canvas.draw(
//			new Segment2D(
//				end.vertex,
//				newEnd.vertex
//			),
//			DrawingSegment2D.withColorDirected(Color.blue)
//		);

		assert newEnd != null;
		assert newEnd != start;
		this.end = newEnd;
		for (SkeletonEvent observer : startObservers) {
			observer.changeOppositeEdgeStart(start, newEnd);
		}
		for (SkeletonEvent observer : endObservers) {
			observer.changeOppositeEdgeEnd(start, newEnd);
		}
	}

	public Node getStart() {
		return start;
	}

	public Node getEnd() {
		return end;
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
//		assert end != nextNode;
//		ImmutableList<Node> lav = ImmutableList.copyOf(nextNode); // NextNode works as Iterable<Node> here.
//		for (SkeletonEvent intersection : observers) {
//			TODO: Do we have to copy observers?
//			We have to copy observers because otherwise that list
//			will be concurrently modified inside this loop.
//			if (lav.contains(intersection.va)) {
//				intersection.changeOppositeEdgeStart(end, nextNode);
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
