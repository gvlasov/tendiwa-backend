package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;

public class StepUnitedWith {

	private final IntimateRectanglePointer pointer;

	public StepUnitedWith(IntimateRectanglePointer pointer) {
		this.pointer = pointer;
	}

	public StepUnitedWithFromSide fromSide(CardinalDirection side) {
		return new StepUnitedWithFromSide(pointer, side);
	}

}
