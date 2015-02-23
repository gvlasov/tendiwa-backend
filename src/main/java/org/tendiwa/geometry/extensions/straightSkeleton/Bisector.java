package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.*;

import javax.annotation.Nonnull;

public class Bisector {
	public static final int DEFAULT_SEGMENT_LENGTH = 40;
	private final Segment2D segment;

	/**
	 * Computes a bisector coming from an intersection of two lines.
	 *
	 * @param previousRay
	 * 	A segment. A ray coming from its start towards its end must intersect a ray coming from
	 * 	{@code currentRay}'s end towards {@code currentRay}'s start.
	 * @param currentRay
	 * 	A segment A ray coming from its end towards its start must intersect a ray coming from
	 * 	{@code previousRay}'s start towards {@code previousRay}'s end.
	 * @param vertex
	 * 	A vertex where {@code previousRay} and reverse {@code currentRay} intersect.
	 * @param toLeft
	 * 	Which one of two possible bisectors we want to compute: the one that is in the left half-plane
	 * 	defined by {@code previousRay}, or the one in the right.
	 */
	public Bisector(Segment2D previousRay, Segment2D currentRay, Point2D vertex, boolean toLeft) {
		assert !previousRay.equals(currentRay);
		if (previousRay.isParallel(currentRay)) {
			this.segment = computeParallelSegment(previousRay, currentRay, vertex, toLeft);
		} else {
			this.segment = computeNonParallelSegment(previousRay, currentRay, vertex, toLeft);
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

	private Segment2D computeParallelSegment(Segment2D previousEdge, Segment2D currentEdge, Point2D vertex, boolean toLeft) {
		Vector2D direction = currentEdge.asVector().rotateQuarterClockwise();
		if (toLeft) {
			direction = direction.multiply(-1);
		}
		return new Segment2D(
			vertex,
			vertex.add(direction)
		);
	}

	private Point2D computeEnd(
		Segment2D previousEdge,
		Segment2D currentEdge,
		Point2D start,
		boolean toLeft
	) {
		Vector2D vPreviousReverse = Vector2D.fromStartToEnd(previousEdge.end, previousEdge.start);
		Vector2D vCurrent = Vector2D.fromStartToEnd(currentEdge.start, currentEdge.end);
		Vector2D sum = vCurrent
			.normalize()
			.add(vPreviousReverse.normalize())
			.normalize()
			.multiply(DEFAULT_SEGMENT_LENGTH);
		boolean additionSign = previousEdge.isLeftOfRay(start.add(sum)) ^ !toLeft;
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
		return new Segment2D(segment.start, new Point2D(segment.start.x + dx, segment.start.y + dy));
	}
}
