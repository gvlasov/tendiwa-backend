package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class Node implements Iterable<Node> {
	List<IntersectionPoint> observers = new ArrayList<>(1);
	Bisector bisector;
	private boolean isProcessed = false; // As said in 1a in [Obdrzalek 1998, paragraph 2.1]
	boolean isReflex;
	Segment2D previousEdge;
	Segment2D currentEdge;
	Node next;
	Node previous;
	final Point2D vertex;
	static TestCanvas canvas;

	Node(Segment2D previousEdge, Segment2D currentEdge, Point2D point) {
		assert !previousEdge.equals(currentEdge);
		this.previousEdge = previousEdge;
		this.currentEdge = currentEdge;
		this.vertex = point;
	}

	/**
	 * Makes this point processed
	 */
	void setProcessed() {
		isProcessed = true;
	}

	boolean isProcessed() {
		return isProcessed;
	}

	public void computeReflexAndBisector() {
		assert bisector == null;
		isReflex = isReflex(
			new Segment2D(
				previous.vertex,
				vertex
			),
			new Segment2D(
				vertex,
				next.vertex
			)
		);
		bisector = new Bisector(previousEdge, currentEdge, vertex, isReflex);
	}

	/**
	 * Finds if two edges going counter-clockwise make a convex or a reflex angle.
	 *
	 * @param previousEdge
	 * 	An edge.
	 * @param currentEdge
	 * 	An edge coming from {@code previousEdge}'s end.
	 * @return True if the angle to the left between two edges > Math.PI
	 */
	private boolean isReflex(Segment2D previousEdge, Segment2D currentEdge) {
		return MinimumCycleBasis.perpDotProduct(
			new double[]{previousEdge.dx(), previousEdge.dy()},
			new double[]{currentEdge.dx(), currentEdge.dy()}
		) > 0;
	}


	void connectWithPrevious(Node previous) {
		assert previous != this;
		this.previous = previous;
		previous.next = this;
	}

	@Override
	public Iterator<Node> iterator() {
		return new Iterator<Node>() {
			Node start = Node.this;
			Node node = Node.this.previous;
			int i = 0;

			@Override
			public boolean hasNext() {
				return node.next != start || i == 0;
			}

			@Override
			public Node next() {
				node = node.next;
				if (i++ > 100) {
					throw new RuntimeException("Too many iterations");
				}
				return node;
			}
		};
	}

	public void addObserver(IntersectionPoint intersectionPoint) {
		observers.add(intersectionPoint);
	}

	/**
	 * Notifies previously computed split events of change in their start points.
	 *
	 * @param nextNode
	 */
	public void notifyObservers(Node nextNode) {
		notifyObservers(nextNode, ImmutableList.copyOf(observers));
	}

	public void notifyObservers(Node nextNode, List<IntersectionPoint> observers) {
		assert nextNode != null;
		assert this != nextNode;
		ImmutableList<Node> lav = ImmutableList.copyOf(nextNode); // NextNode works as Iterable<Node> here.
		for (IntersectionPoint intersection : observers) {
			// TODO: Do we have to copy observers?
			// We have to copy observers because otherwise that list
			// will be concurrently modified inside this loop.
//			if (lav.contains(intersection.va)) {
				intersection.changeOppositeEdgeStart(this, nextNode);
//			}
		}
	}

	public List<IntersectionPoint> copyObservers() {
		return ImmutableList.copyOf(observers);
	}

	public void removeObserver(IntersectionPoint observer) {
		assert observers.contains(observer);
		observers.remove(observer);
	}
}
