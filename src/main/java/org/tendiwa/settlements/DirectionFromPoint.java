package org.tendiwa.settlements;

import org.tendiwa.geometry.Point2D;

public class DirectionFromPoint {
	public final Point2D node;
	public final double direction;

	DirectionFromPoint(Point2D node, double direction) {
		this.node = node;
		this.direction = direction;
	}
}
