package org.tendiwa.settlements;

import org.tendiwa.geometry.Point2D;

/**
 * [Kelly 4.3.3.3]
 * <p>
 * Relative position of a point on a 2d segment.
 */
final class NodePosition {
	/**
	 * Position of a projection of point {@code point} to a line.
	 * <p>
	 * {@code r == 0} means {@code point == a}, {@code r == 1} means {@code point == b}.
	 */
	public final double r;
	/**
	 * Position of a point {@code point} relative to a perpendicular of ab.
	 * <p>
	 * {@code s > 0} means point is to the right from a segment (looking from segment start to segment end),
	 * {@code s < 0} means point is to the left from a segment.
	 */
	public final double s;
	/**
	 * Perpendicular distance from {@code point} to segment's line.
	 */
	public final double distance;

	/**
	 * Computes distance from a point to a line.
	 * <p>
	 * Algorithm is described by O'Rourke at http://www.faqs.org/faqs/graphics/algorithms-faq/ in subject 1.02
	 *
	 * @param segmentStart
	 * 	Start of a segment.
	 * @param segmentEnd
	 * 	End of a segment.
	 * @param point
	 * 	A point whose relative location of a segment is to be found.
	 */
	public NodePosition(Point2D segmentStart, Point2D segmentEnd, Point2D point) {
		// TODO: We can get rid of sqrt() here in distanceTo()
		double l = segmentStart.distanceTo(segmentEnd);
		r = ((point.x - segmentStart.x) * (segmentEnd.x - segmentStart.x)
			+ (point.y - segmentStart.y) * (segmentEnd.y - segmentStart.y))
			/ (l * l);
		s = ((segmentStart.y - point.y) * (segmentEnd.x - segmentStart.x)
			- (segmentStart.x - point.x) * (segmentEnd.y - segmentStart.y))
			/ (l * l);
		distance = Math.abs(s) * l;
	}
}
