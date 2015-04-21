package org.tendiwa.geometry;

import org.tendiwa.core.OrdinalDirection;

final class RectangleInCicrle {
	private final Rectangle rectangle;
	private final Circle circle;

	public RectangleInCicrle(
		Rectangle rectangle,
		Circle circle
	) {
		this.rectangle = rectangle;
		this.circle = circle;
	}

	/**
	 * Checks if all cells of this rectangle are inside a particular circle.
	 */
	public boolean isInCircle() {
		return cornerIsInCircle(OrdinalDirection.NW)
			&& cornerIsInCircle(OrdinalDirection.NE)
			&& cornerIsInCircle(OrdinalDirection.SE)
			&& cornerIsInCircle(OrdinalDirection.SW);
	}

	private boolean cornerIsInCircle(OrdinalDirection corner) {
		return circle.contains(new RectangleCorner(rectangle, corner).toPoint());
	}

}
