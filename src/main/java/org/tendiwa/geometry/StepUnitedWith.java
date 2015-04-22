package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;

final class StepUnitedWith {

	private final RectanglePointer pointer;

	StepUnitedWith(RectanglePointer pointer) {
		this.pointer = pointer;
	}

	public StepUnitedWithFromSide fromSide(CardinalDirection side) {
		return new StepUnitedWithFromSide(pointer, side);
	}

}
