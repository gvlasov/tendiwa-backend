package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableList;
import com.sun.xml.internal.bind.v2.TODO;

import java.util.ArrayList;
import java.util.List;

/**
 * This class tracks the movement of a start point of a segment that sweeps a polygon's face.
 */
public class OppositeEdgeStartMovement {
	private Node start;
	private Node end;
	private List<IntersectionPoint> observers = new ArrayList<>(1);

	public OppositeEdgeStartMovement(Node start) {
		assert start != null;
		this.start = start;
		this.end = start;
	}

	public void moveTo(Node newEnd) {
		assert newEnd != null;
		assert newEnd != start;
//		assert newEnd != end;
		this.end = newEnd;
	}

	public Node getStart() {
		return start;
	}

	public Node getEnd() {
		return end;
	}

	public void addObserver(IntersectionPoint intersectionPoint) {
		observers.add(intersectionPoint);
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
//	public void notifyObservers(Node nextNode, List<IntersectionPoint> observers) {
//		assert nextNode != null;
//		assert end != nextNode;
//		ImmutableList<Node> lav = ImmutableList.copyOf(nextNode); // NextNode works as Iterable<Node> here.
//		for (IntersectionPoint intersection : observers) {
//			TODO: Do we have to copy observers?
//			We have to copy observers because otherwise that list
//			will be concurrently modified inside this loop.
//			if (lav.contains(intersection.va)) {
//				intersection.changeOppositeEdgeStart(end, nextNode);
//			}
//		}
//	}
//
//	public List<IntersectionPoint> copyObservers() {
//		return ImmutableList.copyOf(observers);
//	}

	public void removeObserver(IntersectionPoint observer) {
		assert observers.contains(observer);
		observers.remove(observer);
	}
}
