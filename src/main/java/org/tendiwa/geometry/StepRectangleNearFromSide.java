package org.tendiwa.geometry;

import org.tendiwa.core.RelativeCardinalDirection;

class StepRectangleNearFromSide {
	private final Rectangle rectangle;
	private final RectanglePointer pointer;
	private final RelativeCardinalDirection side;

	StepRectangleNearFromSide(Rectangle rectangle, RectanglePointer pointer, RelativeCardinalDirection side) {
		this.rectangle = rectangle;
		this.pointer = pointer;
		this.side = side;
	}

	public RectangleNearFromSideInMiddlePlacer inMiddle() {
		return new RectangleNearFromSideInMiddlePlacer(rectangle, pointer, side);
	}
}
