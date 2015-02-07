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
}
