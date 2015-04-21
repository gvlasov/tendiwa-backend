package org.tendiwa.geometry;

import org.tendiwa.core.RelativeCardinalDirection;

public class RectangleNearFromSideInMiddlePlacer implements Placement {
	private final IntimateRectanglePointer pointer;
	private final RelativeCardinalDirection side;

	RectangleNearFromSideInMiddlePlacer(Rectangle rectangle, IntimateRectanglePointer pointer, RelativeCardinalDirection side) {
		this.pointer = pointer;
		this.side = side;
	}

	@Override
	public void placeIn(RectSet rectSet, RectangleSystemBuilder builder) {
		throw new UnsupportedOperationException();
	}
}
