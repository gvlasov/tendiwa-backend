package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

final class Ray {
	public final Point2D start;
	public final double direction;

	Ray(Point2D start, double direction) {
		this.start = start;
		this.direction = direction;
	}

	public Point2D placeEnd(double snapSize) {
		return start.moveBy(
			Math.cos(direction) * snapSize,
			Math.sin(direction) * snapSize
		);
	}

	public Ray changeDirection(double newDirection) {
		return new Ray(start, newDirection);
	}

	public Ray changeStart(Point2D newStart) {
		return new Ray(newStart, direction);
	}
}
