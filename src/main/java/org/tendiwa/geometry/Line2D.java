package org.tendiwa.geometry;

public class Line2D {
	private final double a;
	private final double b;
	private final double c;

	/**
	 * Creates a new Line2D defined by 2 points.
	 *
	 * @param ax
	 * 	X-coordinate of first point.
	 * @param ay
	 * 	Y-coordinate of first point.
	 * @param bx
	 * 	X-coordinate of second point.
	 * @param by
	 * 	Y-coordinate of second point.
	 * @throws java.lang.IllegalArgumentException
	 * 	if points are equal.
	 */
	public Line2D(double ax, double ay, double bx, double by) {
		if (ax == bx && ay == by) {
			throw new IllegalArgumentException("Can't construct a line if given points are equal (" + ax + ":" + ay + ")");
		}
		this.a = ay - by;
		this.b = bx - ax;
		this.c = ax * by - bx * ay;
	}

	/**
	 * Computes a point where this line intersects another line.
	 *
	 * @param line
	 * 	Another line.
	 * @return null if lines are parallel or equal, otherwise returns intersection point.
	 */
	public Point2D intersectionWith(Line2D line) {
		double zn = det(a, b, line.a, line.b);
		if (Math.abs(zn) < Vectors2D.EPSILON) {
			return null;
		}
		return new BasicPoint2D(
			-det(c, b, line.c, line.b) / zn,
			-det(a, c, line.a, line.c) / zn
		);
	}

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
