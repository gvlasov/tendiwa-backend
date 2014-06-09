package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.LineIntersection;

class Bisector {
	final Segment2D segment;

	Bisector(Segment2D previousEdge, Segment2D currentEdge, Point2D vertex, boolean isReflex) {
		assert !previousEdge.start.equals(previousEdge.end);
		assert !currentEdge.start.equals(currentEdge.end);
		assert !previousEdge.equals(currentEdge);
		if (previousEdge.isParallel(currentEdge)) {
			this.segment = new Segment2D(
				new Point2D(
					(previousEdge.start.x + currentEdge.end.x) / 2,
					(previousEdge.start.y + currentEdge.end.y) / 2
				),
				new Point2D(
					(previousEdge.end.x + currentEdge.start.x) / 2,
					(previousEdge.end.y + currentEdge.start.y) / 2
				)
			);
		} else {
//			Point2D edgeIntersection = new LineIntersection(previousEdge, currentEdge).getIntersectionPoint();
			this.segment = new Segment2D(
				vertex,
				computeEnd(previousEdge, currentEdge, vertex, isReflex)
			);
		}
	}

	private Point2D computeEnd(
		Segment2D previousEdge,
		Segment2D currentEdge,
		Point2D start,
		boolean isReflex
	) {
//		return new Point2D(
//			start.x + (-previousEdge.dx() / previousEdge.length() + currentEdge.dx()
//				/ currentEdge.length()) * 40 * (isReflex ? -1 : 1),
//			start.y + (-previousEdge.dy() / previousEdge.length() + currentEdge.dy()
//				/ currentEdge.length()) * 40 * (isReflex ? -1 : 1)
//		);

		Point2D vPrevious = previousEdge.start.subtract(previousEdge.end);
		Point2D vCurrent = currentEdge.end.subtract(currentEdge.start);
		Point2D sum = vCurrent.normalize().add(vPrevious.normalize());
		sum = sum.normalize().multiply(40); // Normalized bisector direction
		boolean isStart = previousEdge.end.equals(start)
			&& currentEdge.start.equals(start);
		boolean inFront = isStart || isIntersectionInFrontOfBisectorStart(previousEdge, currentEdge);
		if (isStart && isReflex) {
			inFront = false;
		}
		if (inFront) {
			return start.add(sum);
		} else {
			return start.subtract(sum);
		}
	}

	private boolean isIntersectionInFrontOfBisectorStart(Segment2D previousEdge, Segment2D currentEdge) {
		Segment2D reversePrevious = previousEdge.reverse();
		return new LineIntersection(
			reversePrevious,
			currentEdge
		).r < 0 || new LineIntersection(currentEdge, reversePrevious).r < 0;
	}

	LineIntersection intersectionWith(Bisector bisector) {
		return new LineIntersection(
			segment.start,
			segment.end,
			bisector.segment
		);
	}

}
