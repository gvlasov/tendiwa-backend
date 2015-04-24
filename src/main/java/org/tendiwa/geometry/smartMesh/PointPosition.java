package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.BasicPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

/**
 * [Kelly 4.3.3.3]
 * <p>
 * Relative position of a point on a 2d segment.
 */
final class PointPosition {
	/**
	 * Position of a projection of point {@code point} to a line.
	 * <p>
	 * {@code r == 0} means {@code point == segmentStart}, {@code r == 1} means {@code point == segmentEnd}.
	 */
	final double r;
	/**
	 * Position of a point {@code point} relative to a perpendicular of ab.
	 * <p>
	 * {@code s > 0} means point is to the right from a segment (looking from segment start to segment end),
	 * {@code s < 0} means point is to the left from a segment.
	 */
	final double s;
	/**
	 * Perpendicular distance from {@code point} to segment's line.
	 */
	final double distance;

	/**
	 * Computes distance from a point to a line.
	 * <p>
	 * Algorithm is <a href="http://www.faqs.org/faqs/graphics/algorithms-faq/">described by O'Rourke</a> in subject
	 * 1.02
	 *
	 * @param segmentStart
	 * 	Start of a segment.
	 * @param segmentEnd
	 * 	End of a segment.
	 * @param point
	 * 	A point whose relative location of a segment is to be found.
	 */
	PointPosition(Point2D segmentStart, Point2D segmentEnd, Point2D point) {
		double l = segmentStart.distanceTo(segmentEnd);
		r = ((point.x() - segmentStart.x()) * (segmentEnd.x() - segmentStart.x())
			+ (point.y() - segmentStart.y()) * (segmentEnd.y() - segmentStart.y()))
			/ (l * l);
		s = ((segmentStart.y() - point.y()) * (segmentEnd.x() - segmentStart.x())
			- (segmentStart.x() - point.x()) * (segmentEnd.y() - segmentStart.y()))
			/ (l * l);
		distance = Math.abs(s) * l;
	}
	Point2D pointOnSegment(Segment2D segment) {
		return new BasicPoint2D(
			segment.start().x() + r * (segment.end().x() - segment.start().x()),
			segment.start().y() + r * (segment.end().y() - segment.start().y())
		);
	}
}
