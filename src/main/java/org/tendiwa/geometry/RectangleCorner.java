package org.tendiwa.geometry;

import org.tendiwa.core.OrdinalDirection;

final class RectangleCorner extends Cell_Wr {
	protected RectangleCorner(Rectangle rectangle, OrdinalDirection corner) {
		super(
			xCoordinate(rectangle, corner),
			yCoordinate(rectangle, corner)
		);
	}

	private static int yCoordinate(Rectangle rectangle, OrdinalDirection corner) {
		switch (corner) {
			case NW:
			case NE:
				return rectangle.y();
			case SW:
			case SE:
				return rectangle.maxY();
			default:
				throw new TwoDimensionalWorldConstraintViolation();
		}
	}

	private static int xCoordinate(Rectangle rectangle, OrdinalDirection corner) {
		switch (corner) {
			case NW:
			case SW:
				return rectangle.x();
			case NE:
			case SE:
				return rectangle.maxX();
			default:
				throw new TwoDimensionalWorldConstraintViolation();
		}
	}
}
