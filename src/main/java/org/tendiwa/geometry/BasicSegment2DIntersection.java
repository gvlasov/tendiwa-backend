package org.tendiwa.geometry;

import java.util.Optional;

final class BasicSegment2DIntersection implements StraightLineIntersection {
	private final Segment2D a;
	private final Segment2D b;

	BasicSegment2DIntersection(
		Segment2D a,
		Segment2D b
	) {

		this.a = a;
		this.b = b;
	}

	/**
	 * Finds a point of intersection between this line and another line.
	 * <p>
	 * An intersection at ends of lines doesn't count for an intersection.
	 *
	 * @return A Point2D where these two lines intersect, or null if lines don't intersect.
	 */
	public Optional<Point2D> point() {
		RayIntersection lineIntersection = new RayIntersection(
			a.start(),
			a.end(),
			b
		);
		if (!lineIntersection.segmentsIntersect()) {
			return Optional.empty();
		}
		return Optional.of(lineIntersection.commonPoint());
	}

	/**
	 * Checks if this segment intersects another segment. This is less expensive than finding the intersection point
	 * with {@link #intersection(Segment2D)}.
	 * <p>
	 * An intersection at ends of lines doesn't count for an intersection.
	 *
	 * @return true if lines intersect, false otherwise.
	 * @see #intersection(Segment2D)
	 */
	public boolean intersect() {
		return new RayIntersection(a.start(), a.end(), b).segmentsIntersect();
	}
}
