package org.tendiwa.geometry;

public final class BasicLine2D implements Line2D {
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
	public BasicLine2D(double ax, double ay, double bx, double by) {
		if (ax == bx && ay == by) {
			throw new IllegalArgumentException("Can't construct a line if given points are equal (" + ax + ":" + ay + ")");
		}
		this.a = ay - by;
		this.b = bx - ax;
		this.c = ax * by - bx * ay;
	}

	@Override
	public double a() {
		return a;
	}

	@Override
	public double b() {
		return b;
	}

	@Override
	public double c() {
		return c;
	}
}
