package org.tendiwa.core;

public class StepAwayFrom {
    private final RectanglePointer pointer;

    public StepAwayFrom(RectanglePointer pointer) {
        this.pointer = pointer;
    }
    public StepAwayFromFromSide fromSide(CardinalDirection side) {
        return new StepAwayFromFromSide(pointer, side);
    }
}
