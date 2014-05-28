package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimumCycleBasis;

class Node {
	final Bisector bisector;
	boolean active = true; // As said in 1a in [Obdrzalek 1998, paragraph 2.1]
	final boolean isReflex;
	Segment2D previousEdge;
	Segment2D currentEdge;
	Node next;
	Node previous;
	final Point2D vertex;

	Node(Segment2D previousEdge, Segment2D currentEdge, Node previous) {
		assert currentEdge.start.equals(previousEdge.end);
		this.previous = previous;
		this.previousEdge = previousEdge;
		this.currentEdge = currentEdge;
		this.vertex = currentEdge.start;
		if (previous != null) {
			connect(previous);
		}
		bisector = new Bisector(previousEdge, currentEdge);
		isReflex = isReflex(previousEdge, currentEdge);
	}

	/**
	 * Finds if two edges going counter-clockwise make a convex or a reflex angle.
	 *
	 * @param previousEdge
	 * @param currentEdge
	 * @return True if the angle between two edges
	 */
	private boolean isReflex(Segment2D previousEdge, Segment2D currentEdge) {
		return MinimumCycleBasis.perpDotProduct(
			new double[]{previousEdge.dx(), previousEdge.dy()},
			new double[]{currentEdge.dx(), currentEdge.dy()}
		) > 0;
	}


	void connect(Node previous) {
		this.previous = previous;
		previous.next = this;
	}

}
