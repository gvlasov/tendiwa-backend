package org.tendiwa.settlements;

import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;

/**
 * Finds intersection of two <i>lines</i> defined by <i>segments</i> that lie on those lines.
 * <p>
 * Note that if lines intersect doesn't mean that segments intersect.
 */
public class LineIntersection {
	public final boolean intersects;
	/**
	 * Relative distance from {@code sourceNode} to {@code targetPoint}, 0.0 means intersection is at {@code
	 * sourceNode}, 1.0 means it's at target point. Note that {@code r} is computed even if lines are parallel,
	 * in which case {@code r == Infinity}.
	 * <p>
	 * If 0 <= r <= 1, then not only lines intersect, but segments too.
	 */
	public final double r;

	public final double s;
	private final Point2D sourceNode;
	private final Point2D targetPoint;

	public LineIntersection(Point2D sourceNode, Point2D targetPoint, Segment2D segment) {
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
		if (denom == 0) {
			throw new GeometryException(
				"Lines " + new Segment2D(sourceNode, targetPoint) + " and " + segment + " are  parallel"
			);
		}
		Point2D ca = new Point2D(
			sourceNode.x - segment.start.x,
			sourceNode.y - segment.start.y
		);
		r = ((ca.y * cd.x) - (ca.x * cd.y)) / denom;
		s = ((ca.y * ab.x) - (ca.x * ab.y)) / denom;
		intersects = (denom != 0) && !(r == 0 && s == 0);
	}

	public LineIntersection(Segment2D a, Segment2D b) {
		this(a.start, a.end, b);
	}

	public Point2D getIntersectionPoint() {
		return new Point2D(
			sourceNode.x + (targetPoint.x - sourceNode.x) * r,
			sourceNode.y + (targetPoint.y - sourceNode.y) * r
		);
	}

	public boolean segmentsIntersect() {
		return r > 0 && r < 1 && s == 0;
	}
}
