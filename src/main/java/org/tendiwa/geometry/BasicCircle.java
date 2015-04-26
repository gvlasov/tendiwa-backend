package org.tendiwa.geometry;

public final class BasicCircle implements Circle {
	private final Point2D center;
	private final double radius;

	public BasicCircle(
		Point2D center,
		double radius
	) {
		this.center = center;
		this.radius = radius;
	}
	@Override
	public Point2D center() {
		return center;
	}

	@Override
	public double radius() {
		return radius;
	}
}
