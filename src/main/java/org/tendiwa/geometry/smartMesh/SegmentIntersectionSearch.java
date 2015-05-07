package org.tendiwa.geometry.smartMesh;

import org.tendiwa.core.meta.BasicRange;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.RayIntersection;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.Collection;
import java.util.Optional;

final class SegmentIntersectionSearch implements EventSearch {
	private final Collection<Segment2D> segmentsToTest;
	private double minR;
	private final Point2D sourceNode;
	private final Point2D targetNode;
	private SnapToSegment result;

	SegmentIntersectionSearch(
		Point2D sourceNode,
		Point2D targetNode,
		Collection<Segment2D> segmentsToTest
	) {
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.minR = 1;
		this.segmentsToTest = segmentsToTest;
	}

	@Override
	public Optional<PropagationEvent> find() {
		segmentsToTest.forEach(this::tryIntersecting);
		return Optional.ofNullable(result);
	}

	private void tryIntersecting(Segment2D segment) {
		if (segment.oneOfEndsIs(sourceNode) || segment.oneOfEndsIs(targetNode)) {
			return;
		}
		if (isSegmentIntersectionProbable(sourceNode, targetNode, segment.start(), segment.end())) {
			RayIntersection intersection = new RayIntersection(sourceNode, targetNode, segment);
			if (isIntersectionInsideUnsnappedSegment(intersection)) {
				Point2D intersectionPoint = intersection.commonPoint();
				assert !intersectionPoint.equals(sourceNode) : "Commented code below should be used";
//				if (intersectionPoint.equals(sourceNode)) {
//					return new SnapEvent(null, SnapEventType.NO_NODE, null);
//				}
				if (result != null && intersectionPoint.equals(result.target())) {
					return;
				}
				assert !intersectionPoint.equals(segment.end()) : segment.end().hashCode() + " it should have been a " +
					"point snap";
				result = new SnapToSegment(sourceNode, intersectionPoint, segment);
				minR = intersection.r;
			}
		}
	}

	private boolean isIntersectionInsideUnsnappedSegment(RayIntersection intersection) {
		return intersection.r < minR && intersection.r >= 0 && intersection.intersects;
	}

	/**
	 * [Kelly 4.3.3.4]
	 * <p>
	 * In [Kelly 4.3.3.4] there is no pseudocode for this function, it is described in the second paragraph.
	 * <p>
	 * Provides a quick heuristic to see if two lines should be tested for an intersection.
	 *
	 * @param abStart
	 * 	Start of line ab.
	 * @param abEnd
	 * 	End of line ab.
	 * @param cdStart
	 * 	Start of line cd.
	 * @param cdEnd
	 * 	End of line cd. Interchanging arguments for ab and cd should yield the same result.
	 * @return true if it is possible
	 */
	private boolean isSegmentIntersectionProbable(
		Point2D abStart,
		Point2D abEnd,
		Point2D cdStart,
		Point2D cdEnd
	) {
		// TODO: Replace points with segments
		PointPosition pointPosition = new PointPosition(abStart, abEnd, cdStart);
		PointPosition pointPosition2 = new PointPosition(abStart, abEnd, cdEnd);
		if (Math.signum(pointPosition.s) == Math.signum(pointPosition2.s)) {
			return false;
		}
		/*
		 * A very important note: in [Kelly 4.3.3.4] it is said
         * that an intersection within the bounds of ab is only probable
         * when points of cd are on <i>opposing extensions</i> of ab;.
         * however, actually instead they must be <i>not on the same extension</i>.
         * The difference is that in my version (and in real cases) a line CD with C on an extension
         * and 0<D.r<1 should be tested for an intersection too.
         */
		return BasicRange.contains(0, 1, pointPosition.r) && BasicRange.contains(0, 1, pointPosition2.r)
			|| !(pointPosition.r > 1 && pointPosition2.r > 1 || pointPosition.r < 0 && pointPosition2.r < 0);
	}
}