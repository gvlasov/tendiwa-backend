package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;
import org.tendiwa.settlements.RayIntersection;

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
//			Point2D edgeIntersection = new RayIntersection(previousEdge, currentEdge).getIntersectionPoint();
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
		Vector2D vPrevious = Vector2D.fromStartToEnd(previousEdge.end, previousEdge.start);
		Vector2D vCurrent = Vector2D.fromStartToEnd(currentEdge.start, currentEdge.end);
		Vector2D sum = vCurrent.normalize().add(vPrevious.normalize());
		sum = sum.normalize().multiply(40); // Normalized bisector direction
		boolean belongsToBothEdges = previousEdge.end.equals(start) && currentEdge.start.equals(start);
		boolean additionSign;
		if (belongsToBothEdges) {
			additionSign = !isReflex;
		} else {
//			additionSign = isIntersectionInFrontOfBisectorStart(previousEdge, currentEdge);
			additionSign = true;
		}
		if (additionSign) {
			return start.add(sum);
		} else {
			return start.subtract(sum);
		}
	}

	private boolean isIntersectionInFrontOfBisectorStart(Segment2D previousEdge, Segment2D currentEdge) {
		Segment2D reversePrevious = previousEdge.reverse();
		return new RayIntersection(
			reversePrevious,
			currentEdge
		).r < 0 || new RayIntersection(currentEdge, reversePrevious).r < 0;
	}

	RayIntersection intersectionWith(Bisector bisector) {
		return new RayIntersection(
			segment.start,
			segment.end,
			bisector.segment
		);
	}

}
