package org.tendiwa.geometry;

/**
 * Finds intersection of two <i>rays</i>, both defined by the start of a ray and some other point on that ray.
 * <p>
 * Note that if rays intersect doesn't mean that segments intersect.
 */
public class RayIntersection {
	public final boolean intersects;
	/**
	 * Relative distance from start of the first ray to intersection point, 0.0 means intersection is at {@code
	 * sourceNode}, 1.0 means it's at target point. Note that {@code r} is computed even if rays are parallel,
	 * in which case {@code r == Infinity}.
	 */
	public final double r;

	public final double s;
	private final Point2D sourceNode;
	private final Point2D targetPoint;

	public RayIntersection(Point2D sourceNode, Point2D targetPoint, Segment2D segment) {
		if (sourceNode.equals(targetPoint)) {
			throw new IllegalArgumentException("There can't be zero distance between points");
		}
		if (segment.start.equals(segment.end)) {
			throw new IllegalArgumentException("Segment can't be zero length");
		}
		this.sourceNode = sourceNode;
		this.targetPoint = targetPoint;

		Point2D ab = new Point2D(
			targetPoint.x - sourceNode.x,
			targetPoint.y - sourceNode.y
		);
		Point2D cd = new Point2D(
			segment.end.x - segment.start.x,
			segment.end.y - segment.start.y
		);
		double denom = (ab.x * cd.y) - (ab.y * cd.x);
		// TODO: Is computation of parallel rays needed or not?
//		if (denom == 0) {
//			throw new GeometryException(
//				"Rays " + new Segment2D(sourceNode, targetPoint) + " and " + segment + " are  parallel"
//			);
//		}
		Point2D ca = new Point2D(
			sourceNode.x - segment.start.x,
			sourceNode.y - segment.start.y
		);
		r = ((ca.y * cd.x) - (ca.x * cd.y)) / denom;
		s = ((ca.y * ab.x) - (ca.x * ab.y)) / denom;
		intersects = (denom != 0) && !(r == 0 && s == 0);
	}

	public RayIntersection(Segment2D a, Segment2D b) {
		this(a.start, a.end, b);
	}

	public Point2D getLinesIntersectionPoint() {
		if (!intersects) {
			throw new GeometryException("Trying to find intersection point of two parallel lines");
		}
		assert Double.isFinite(r);
		return new Point2D(
			sourceNode.x + (targetPoint.x - sourceNode.x) * r,
			sourceNode.y + (targetPoint.y - sourceNode.y) * r
		);
	}

	private final static double ALMOST_1 = 1 - Vectors2D.EPSILON;

	public boolean segmentsIntersect() {
		return r > Vectors2D.EPSILON && r < ALMOST_1 && s > Vectors2D.EPSILON && s < ALMOST_1;
	}
}
