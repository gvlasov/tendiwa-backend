package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;

final class RectangleToSegmentDirection {
	/**
	 * Returns a {@link org.tendiwa.core.CardinalDirection} you need to go in from {@code rectangle}'s side to get to
	 * {@code segment}.
	 *
	 * @param rectangle
	 * @param segment
	 * @return
	 */
	public static CardinalDirection getDirectionToSegment(Segment2D segment, Rectangle rectangle) {
		assert !Recs.rectangleIntersectsSegment(rectangle, segment);
		// For line equation y=k*x+b
		double dy = segment.dy();
		// If segment is parallel to one of axes, then we need a separate case.
		if (dy == 0) {
			// Parallel to x axis
			if (Math.abs(segment.start.y - rectangle.y) < Math.abs(segment.start.y - rectangle.getMaxY())) {
				return CardinalDirection.N;
			} else {
				return CardinalDirection.S;
			}
		}
		double dx = segment.dx();
		if (dx == 0) {
			// Parallel to y axis
			if (Math.abs(segment.start.x - rectangle.x) < Math.abs(segment.start.x - rectangle.getMaxX())) {
				return CardinalDirection.W;
			} else {
				return CardinalDirection.E;
			}
		}
		Point2D closerEnd = getCloserEnd(segment, rectangle);
		if (pointIsFacingRectangleSide(closerEnd, rectangle)) {
			return getCardinalDirectionToPoint(closerEnd, rectangle);
		}
		Point2D fartherEnd = closerEnd == segment.start ? segment.end : segment.start;
		if (pointIsFacingRectangleSide(fartherEnd, rectangle)) {
			return getCardinalDirectionToPoint(fartherEnd, rectangle);
		}
		// Find k for y=k*x+b
		double k = Math.abs(dy / segment.dx());
		// Find b for y=k*x+b
		double b = segment.start.y - k * segment.start.x;
		if (k > 1) {
			// Near-vertical segment case
			// Pick any y on vertical sides of a rectangle
			double recY = rectangle.getCenterY();
			// Find x where segment intersects line y=recY
			double segmentX = (recY - b) / k;
			// Find which side is closer
			if (Math.abs(segmentX - rectangle.x) < Math.abs(segmentX - rectangle.getMaxX())) {
				return CardinalDirection.W;
			} else {
				return CardinalDirection.E;
			}
		} else {
			// Near-horizontal segment case
			double recX = rectangle.getCenterX();
			// Find y on segment's line at recX
			double segmentY = k * recX + b;
			if (Math.abs(segmentY - rectangle.y) < Math.abs(segmentY - rectangle.getMaxY())) {
				return CardinalDirection.N;
			} else {
				return CardinalDirection.S;
			}
		}
	}

	private static CardinalDirection getCardinalDirectionToPoint(Point2D point, Rectangle rectangle) {
		if (point.x < rectangle.x) {
			return CardinalDirection.W;
		}
		if (point.x > rectangle.getMaxX()) {
			return CardinalDirection.E;
		}
		if (point.y < rectangle.y) {
			return CardinalDirection.N;
		}
		assert point.y > rectangle.getMaxY();
		return CardinalDirection.S;
	}

	/**
	 * Checks if a point is inside the area
	 * bounded by an infinite cross of axis-parallel lines
	 * coming from an axis-parallel rectangle's sides,
	 * excluding the rectangle itself.
	 * <p>
	 * This method returns true for the green point, false for cyan points:
	 * <p>
	 * The area is in red, the rectangle defining area is in blue. This method returns true for the green point,
	 * and false for the black points:
	 * <p>
	 * <img src="http://tendiwa.org/doc-illustrations/points-in-front-of-axis-parallel-rectangle-sides.png" />
	 *
	 * @param point
	 * 	A point to check.
	 * @param rectangle
	 * 	A rectangle that defines area.
	 * @return
	 */
	private static boolean pointIsFacingRectangleSide(
		Point2D point,
		Rectangle rectangle
	) {
		if (rectangle.containsDoubleStrict(point.x, point.y)) {
			return false;
		}
		return point.y >= rectangle.y && point.y <= rectangle.getMaxY()
			|| point.x >= rectangle.x && point.x <= rectangle.getMaxX();
	}

	/**
	 * Returns the end of a {@code segment} that is closer to a {@code rectangle}'s center in Chebyshov metric.
	 * <p>
	 * It might seem that in in cases where this method is used a proximity to rectangle's <i>border</i> would be a
	 * better criteria, but actually Chebyshov distance is enough here (and most importantly, it computes quicker).
	 *
	 * @param segment
	 * 	A segment to find closer end of.
	 * @param rectangle
	 * 	A rectangle for which we compute distance to segments' ends.
	 * @return
	 */
	private static Point2D getCloserEnd(Segment2D segment, Rectangle rectangle) {
		Point2D rectangleCenter = rectangle.getCenterPoint();
		if (segment.start.chebyshovDistanceTo(rectangleCenter) < segment.end.chebyshovDistanceTo(rectangleCenter)) {
			return segment.start;
		} else {
			return segment.end;
		}
	}
}
