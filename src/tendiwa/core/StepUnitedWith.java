package tendiwa.core;

public class StepUnitedWith {

private final RectanglePointer pointer;

public StepUnitedWith(RectanglePointer pointer) {
	this.pointer = pointer;
}
public StepUnitedWithFromSide fromSide(CardinalDirection side) {
	return new StepUnitedWithFromSide(pointer, side);
}

}
