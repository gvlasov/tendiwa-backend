package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimumCycleBasis;

class Node {
	Bisector bisector;
	boolean isProcessed = false; // As said in 1a in [Obdrzalek 1998, paragraph 2.1]
	boolean isReflex;
	Segment2D previousEdge;
	Segment2D currentEdge;
	Node next;
	Node previous;
	final Point2D vertex;

	Node(Segment2D previousEdge, Segment2D currentEdge, Point2D point) {
		this.previousEdge = previousEdge;
		this.currentEdge = currentEdge;
		this.vertex = point;
	}

	public void computeReflexAndBisector() {
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
		this.previous = previous;
		previous.next = this;
	}

}
