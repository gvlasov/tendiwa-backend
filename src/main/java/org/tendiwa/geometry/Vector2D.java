package org.tendiwa.geometry;

public interface Vector2D extends Position2D {
	/**
	 * Sums two vectors.
	 *
	 * @param another
	 * 	A vector.
	 * @return Third vector that is a sum of two vectors.
	 */
	public default Vector2D add(Vector2D another) {
		return new BasicPoint2D(x() + another.x(), y() + another.y());
	}

	/**
	 * Subtracts one vector from this one.
	 *
	 * @param another
	 * 	A vector.
	 * @return Third vector that is a sum of two vectors.
	 */
	public default Vector2D subtract(Vector2D another) {
		return new BasicPoint2D(x() - another.x(), y() - another.y());
	}

	/**
	 * Returns magnitude of a vector (length of vector in case of a vector in 2d Euclidean space).
	 *
	 * @return Magnitude of a vector.
	 */
	public default double magnitude() {
		return Math.sqrt(x() * x() + y() * y());
	}

	/**
	 * Divides this vector by a scalar, producing a new vector.
	 *
	 * @param scalar
	 * 	A scalar to divide by.
	 * @return A new vector.
	 */
	public default Vector2D divide(double scalar) {
		return new BasicPoint2D(x() / scalar, y() / scalar);
	}

	public default Vector2D normalize() {
		return divide(magnitude());
	}

	default Vector2D multiply(double magnitude) {
		return new BasicPoint2D(x() * magnitude, y() * magnitude);
	}

	public default double dotProduct(Vector2D vector) {
		return (x() * vector.x() + y() * vector.y());
	}

	public static Vector2D vector(double x, double y) {
		return new BasicPoint2D(x, y);
	}

	/**
	 * Creates a new point moved from this one by {dx:dy}.
	 *
	 * @param dx
	 * 	Change in x coordinate.
	 * @param dy
	 * 	Change in y coordinate.
	 * @return A new Point2D.
	 */
	public default Vector2D moveBy(double dx, double dy) {
		return new BasicPoint2D(x() + dx, y() + dy);
	}

	/**
	 * Creates a new vector pointing from {@code start} point to {@code end} point.
	 *
	 * @param start
	 * 	Start point.
	 * @param end
	 * 	End point.
	 * @return A vector that is end-start;
	 */
	public static Vector2D fromStartToEnd(Vector2D start, Vector2D end) {
		return end.subtract(start);
	}

	/**
	 * Rotates a vector by 90 degrees clockwise. Clockwise is defined by y-down axis.
	 *
	 * @return A new vector, rotated 90 degrees clockwise from this one.
	 */
	public default Vector2D rotateQuarterClockwise() {
		return new BasicPoint2D(-y(), x());
	}

	public default Vector2D reverse() {
		return new BasicPoint2D(-x(), -y());
	}

	public default Vector2D rotate(double radians) {
		double ca = Math.cos(radians);
		double sa = Math.sin(radians);
		return new BasicPoint2D(ca * x() - sa * y(), sa * x() + ca * y());
	}

	public default boolean isZero() {
		return x() == 0 && y() == 0;
	}

	/**
	 * Checks if clockwise angle between this vector and another vector is {@code >Math.PI}. Relative to angle's
	 * bisector, this vector is considered counter-clockwise, and another is considered clockwise.
	 *
	 * @param cw
	 * 	Another vector.
	 * @return true if the angle between vectors going clockwise from this vector to {@code another} is reflex,
	 * false otherwise.
	 */
	public default boolean makesReflexAngle(Vector2D cw) {
		return cw.perpDotProduct(this) > 0;
	}

	public default double perpDotProduct(Vector2D vector) {
		return x() * vector.y() - y() * vector.x();
	}

	public default boolean isBetweenVectors(Vector2D cw, Vector2D ccw) {
		if (cw.makesReflexAngle(ccw)) {
			return ccw.perpDotProduct(this) < 0 || this.perpDotProduct(cw) < 0;
		} else {
			return cw.perpDotProduct(this) > 0 && this.perpDotProduct(ccw) > 0;
		}
	}

	/**
	 * Finds Chebyshev distance between this cell and another cell.
	 * <p>
	 * Finding Chebyshev distance is much cheaper than finding Euclidean distance with {@link
	 * #distanceTo(BasicPoint2D)}.
	 *
	 * @param point
	 * 	Another point.
	 * @return Chebyshev distance between two cells.
	 */
	public default double chebyshovDistanceTo(Point2D point) {
		return Math.max(Math.abs(point.x() - x()), Math.abs(point.y() - y()));
	}
}
