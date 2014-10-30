package org.tendiwa.settlements.networks;

import org.tendiwa.geometry.Point2D;

final class DirectionFromPoint {
	public final Point2D node;
	public final double direction;

	DirectionFromPoint(Point2D node, double direction) {
		this.node = node;
		this.direction = direction;
	}
}
