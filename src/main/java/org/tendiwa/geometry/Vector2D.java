package org.tendiwa.geometry;

/**
 * Quick and dirty operations with vectors. For performance-critical code you better not use this API (actually,
 * in that case you better not use OOP approach at all).
 */
public interface Vector2D extends Position2D {

	/**
	 * Sums two vectors.
	 *
	 * @param another
	 * 	A vector.
	 * @return Third vector that is a sum of two vectors.
	 */
	public Vector2D add(Vector2D another);

	/**
	 * Subtracts one vector from this one.
	 *
	 * @param another
	 * 	A vector.
	 * @return Third vector that is a sum of two vectors.
	 */
	public Vector2D subtract(Vector2D another);

	/**
	 * Returns magnitude of a vector (length of vector in case of a vector in 2d Euclidean space).
	 *
	 * @return Magnitude of a vector.
	 */
	public default double magnitude() {
		return Math.sqrt(getX() * getX() + getY() * getY());
	}

	/**
	 * Divides this vector by a scalar, producing a new vector.
	 *
	 * @param scalar
	 * 	A scalar to divide by.
	 * @return A new vector.
	 */
	public Vector2D divide(double scalar);

	public default Vector2D normalize() {
		return divide(magnitude());
	}

	Vector2D multiply(double magnitude);

	public default double dotProduct(Vector2D vector) {
		return (getX() * vector.getX() + getY() * vector.getY());
	}

	public static Vector2D vector(double x, double y) {
		return new Point2D(x, y);
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
		return new Point2D(-getY(), getX());
	}

	public default Vector2D reverse() {
		return new Point2D(-getX(), -getY());
	}

	public default Vector2D rotate(double radians) {
		double ca = Math.cos(radians);
		double sa = Math.sin(radians);
		return new Point2D(ca * getX() - sa * getY(), sa * getX() + ca * getY());
	}

	public default boolean isZero() {
		return getX() == 0 && getY() == 0;
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
		return getX() * vector.getY() - getY() * vector.getX();
	}

	public default boolean isBetweenVectors(Vector2D cw, Vector2D ccw) {
		if (cw.makesReflexAngle(ccw)) {
			return ccw.perpDotProduct(this) < 0 || this.perpDotProduct(cw) < 0;
		} else {
			return cw.perpDotProduct(this) > 0 && this.perpDotProduct(ccw) > 0;
		}
	}
}
