package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;
import org.tendiwa.geometry.RayIntersection;

import javax.annotation.Nonnull;

public class Bisector {
	private static final int DEFAULT_SEGMENT_LENGTH = 40;
	private final Segment2D segment;

	public Bisector(Segment2D previousEdge, Segment2D currentEdge, Point2D vertex, boolean isReflex) {
		assert !previousEdge.equals(currentEdge);
		// TODO: Do we compute parallel edges?
		if (previousEdge.isParallel(currentEdge)) {
			this.segment = computeParallelSegment(previousEdge, currentEdge);
		} else {
			this.segment = computeNonParallelSegment(previousEdge, currentEdge, vertex, isReflex);
		}
		assert !segment.start.equals(segment.end);
	}

	private Segment2D computeNonParallelSegment(
		Segment2D previousEdge,
		Segment2D currentEdge,
		Point2D vertex,
		boolean isReflex
	) {
		return new Segment2D(
			vertex,
			computeEnd(previousEdge, currentEdge, vertex, isReflex)
		);
	}

	private Segment2D computeParallelSegment(Segment2D previousEdge, Segment2D currentEdge) {
		return new Segment2D(
			new Point2D(
				(previousEdge.start.x + currentEdge.end.x) / 2,
				(previousEdge.start.y + currentEdge.end.y) / 2
			),
			new Point2D(
				(previousEdge.end.x + currentEdge.start.x) / 2,
				(previousEdge.end.y + currentEdge.start.y) / 2
			)
		);
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
		sum = sum.normalize().multiply(DEFAULT_SEGMENT_LENGTH); // Normalized bisector direction
		boolean belongsToBothEdges = previousEdge.end.equals(start) && currentEdge.start.equals(start);
		boolean additionSign;
		if (belongsToBothEdges) {
			additionSign = !isReflex;
		} else {
			additionSign = true;
		}
		if (additionSign) {
			return start.add(sum);
		} else {
			return start.subtract(sum);
		}
	}

	public RayIntersection intersectionWith(@Nonnull Bisector bisector) {
		try {
			return new RayIntersection(
				segment.start,
				segment.end,
				bisector.segment
			);
		} catch (NullPointerException e) {
			throw e;
		}
	}

	public Segment2D asSegment(double length) {
		if (length == DEFAULT_SEGMENT_LENGTH) {
			return segment;
		}
		double sdx = segment.dx();
		double sdy = segment.dy();
		double dy;
		double dx;
		if (sdy == 0) {
			dy = 0;
			dx = length * Math.signum(sdx);
		} else {
			dy = length / Math.sqrt(sdx * sdx / (sdy * sdy) + 1);
			dx = Math.abs(sdx) * dy / Math.abs(sdy);
			if (sdx < 0) {
				dx = -dx;
			}
			if (sdy < 0) {
				dy = -dy;
			}
		}
		System.out.println(dx + " " + dy + " " + sdx + " " + sdy);
		System.out.println(segment);
		return new Segment2D(segment.start, new Point2D(segment.start.x + dx, segment.start.y + dy));
	}
}
