package org.tendiwa.geometry;

import java.util.Optional;

public interface Line2D {
	/**
	 * Computes a point where this line intersects another line.
	 *
	 * @param line
	 * 	Another line.
	 * @return null if lines are parallel or equal, otherwise returns intersection point.
	 */
	default Optional<Point2D> intersectionWith(Line2D line) {
		double zn = det(a(), b(), line.a(), line.b());
		if (Math.abs(zn) < Vectors2D.EPSILON) {
			return Optional.empty();
		}
		return Optional.of(
			new BasicPoint2D(
				-det(c(), b(), line.c(), line.b()) / zn,
				-det(a(), c(), line.a(), line.c()) / zn
			)
		);
	}

	public double a();

	public double b();

	public double c();

	/**
	 * Computes determinant of a 2×2 matrix.
	 *
	 * @param a
	 * 	a11
	 * @param b
	 * 	a12
	 * @param c
	 * 	a21
	 * @param d
	 * 	a22
	 * @return Determinant of a 2×2 matrix.
	 */
	public static double det(double a, double b, double c, double d) {
		return a * d - b * c;
	}
}
