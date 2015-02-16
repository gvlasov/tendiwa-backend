package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

final class DirectionFromPoint {
	public final Point2D point;
	public final double direction;

	DirectionFromPoint(Point2D point, double direction) {
		this.point = point;
		this.direction = direction;
	}

	public Point2D placeNextPoint(double snapSize) {
		return point.moveBy(
			Math.cos(direction) * snapSize,
			Math.sin(direction) * snapSize
		);
	}

	public DirectionFromPoint changeDirection(double newDirection) {
		return new DirectionFromPoint(
			point,
			newDirection
		);
	}
}
