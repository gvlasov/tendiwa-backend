package org.tendiwa.geometry;

final class RectangleSegmentIntersection {
	private final Rectangle rectangle;
	private final Segment2D segment;

	public RectangleSegmentIntersection(Rectangle rectangle, Segment2D segment) {
		this.rectangle = rectangle;
		this.segment = segment;
	}

	boolean intersect() {
		double pointPosition = pointRelativeToLine(rectangle.x(), rectangle.y(), segment);
		do {
			if (Math.abs(pointPosition) < Vectors2D.EPSILON) {
				break;
			}
			double newPointPosition = pointRelativeToLine(rectangle.maxX(), rectangle.y(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(rectangle.x(), rectangle.maxY(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(rectangle.maxX(), rectangle.maxY(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			return false;
		} while (false);
		double segmentBoundsMin;
		double segmentBoundsMax;
		if (segment.start().x() < segment.end().x()) {
			segmentBoundsMin = segment.start().x();
			segmentBoundsMax = segment.end().x();
		} else {
			segmentBoundsMin = segment.end().x();
			segmentBoundsMax = segment.start().x();
		}
		if (segmentBoundsMax < rectangle.x() || segmentBoundsMin > rectangle.maxX()) {
			return false;
		}
		if (segment.start().y() < segment.end().y()) {
			segmentBoundsMin = segment.start().y();
			segmentBoundsMax = segment.end().y();
		} else {
			segmentBoundsMin = segment.end().y();
			segmentBoundsMax = segment.start().y();
		}
		if (segmentBoundsMax < rectangle.y() || segmentBoundsMin > rectangle.maxY()) {
			return false;
		}
		return true;
	}

	/**
	 * @param x
	 * 	X-coordinate of a point.
	 * @param y
	 * 	X-coordinat of a point.
	 * @param segment
	 * 	A segment.
	 * @return > 0 if point is below line, < 0 if point is above line, 0 if point is on line.
	 */
	private static double pointRelativeToLine(int x, int y, Segment2D segment) {
		return (segment.end().y() - segment.start().y()) * x + (segment.start().x() - segment.end().x()) * y
			+ (segment.end().x() * segment.start().y() - segment.start().x() * segment.end().y());
	}
}
