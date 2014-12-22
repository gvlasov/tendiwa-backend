package org.tendiwa.geometry;

public final class Rectangle2D {
	public final double x;
	public final double y;
	public final double width;
	public final double height;

	public Rectangle2D(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public double getMaxX() {
		return x + width;
	}

	public double getMaxY() {
		return y + height;
	}

	/**
	 * Checks if a rectangle intersects segment.
	 *
	 * @param segment
	 * 	A segment.
	 * @return true if some part of {@code segment} lies inside {@code rectangle}, false otherwise.
	 * @see <a href="http://stackoverflow.com/a/293052/1542343">How to test if a line segment intersects an
	 * axis-aligned rectange in 2D</a>
	 */
	public boolean intersectsSegment(Segment2D segment) {
		double pointPosition = pointRelativeToLine(x, y, segment);
		do {
			if (Math.abs(pointPosition) < Vectors2D.EPSILON) {
				break;
			}
			double newPointPosition = pointRelativeToLine(getMaxX(), y, segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(x, getMaxY(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(getMaxX(), getMaxY(), segment);
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
		if (segment.start.x < segment.end.x) {
			segmentBoundsMin = segment.start.x;
			segmentBoundsMax = segment.end.x;
		} else {
			segmentBoundsMin = segment.end.x;
			segmentBoundsMax = segment.start.x;
		}
		if (segmentBoundsMax < x || segmentBoundsMin > getMaxX()) {
			return false;
		}
		if (segment.start.y < segment.end.y) {
			segmentBoundsMin = segment.start.y;
			segmentBoundsMax = segment.end.y;
		} else {
			segmentBoundsMin = segment.end.y;
			segmentBoundsMax = segment.start.y;
		}
		if (segmentBoundsMax < y || segmentBoundsMin > getMaxY()) {
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
	private double pointRelativeToLine(double x, double y, Segment2D segment) {
		return (segment.end.y - segment.start.y) * x
			+ (segment.start.x - segment.end.x) * y
			+ (segment.end.x * segment.start.y - segment.start.x * segment.end.y);
	}
}
