package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.LineIntersection;

class Bisector {
	final Segment2D segment;


	Bisector(Segment2D previousEdge, Segment2D currentEdge) {
		this.segment = new Segment2D(currentEdge.start, computeEnd(previousEdge, currentEdge));
	}

	private Point2D computeEnd(Segment2D previousEdge, Segment2D currentEdge) {
		return new Point2D(
			currentEdge.start.x - previousEdge.dx() + currentEdge.dx(),
			currentEdge.start.y - previousEdge.dy() + currentEdge.dy()
		);
	}

	LineIntersection intersectionWith(Bisector bisector) {
		return new LineIntersection(
			segment.start,
			segment.end,
			bisector.segment
		);
	}

}
