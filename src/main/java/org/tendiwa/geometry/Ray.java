package org.tendiwa.geometry;

public final class Ray {
	public final Point2D start;
	public final double direction;

	public Ray(Point2D start, double direction) {
		this.start = start;
		this.direction = direction;
	}

	public final Point2D placeEnd(double snapSize) {
		return start.moveBy(
			Math.cos(direction) * snapSize,
			Math.sin(direction) * snapSize
		);
	}

	public final Ray changeDirection(double newDirection) {
		return new Ray(start, newDirection);
	}

	public final Ray changeStart(Point2D newStart) {
		return new Ray(newStart, direction);
	}

	public final Ray inverse() {
		return new Ray(start, Math.PI*2-direction);
	}
}
