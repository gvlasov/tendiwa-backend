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
	public double magnitude();

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

	Point2D multiply(double magnitude);
}
