package org.tendiwa.core;

public class StepAwayFromFromSide {
    private final RectanglePointer pointer;
    private final CardinalDirection side;

    StepAwayFromFromSide(RectanglePointer pointer, CardinalDirection side) {
        this.pointer = pointer;
        this.side = side;
    }
    public StepAwayFromFromSideMargin margin(int margin) {
        return new StepAwayFromFromSideMargin(pointer, side, margin);
    }

}
