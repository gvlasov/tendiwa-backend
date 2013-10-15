package tendiwa.geometry;

public class StepAwayFromFromSideMarginAlign implements Placement {
    private final RectanglePointer pointer;
    private final CardinalDirection side;
    private final int margin;
    private final CardinalDirection alignmentSide;

    StepAwayFromFromSideMarginAlign(RectanglePointer pointer, CardinalDirection side, int margin, CardinalDirection alignmentSide) {
        this.pointer = pointer;
        this.side = side;
        this.margin = margin;
        this.alignmentSide = alignmentSide;
    }

    @Override
    public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
        EnhancedRectangle placeableBounds = placeable.getBounds();
        EnhancedRectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
        int staticCoord = existingRec.getStaticCoordOfSide(side) + (builder.rs.borderWidth + 1 + margin) * side.getGrowing();
        if (side == Directions.N) {
            staticCoord -= placeableBounds.height;
        } else if (side == Directions.W) {
            staticCoord -= placeableBounds.width;
        }
        int dynamicCoord = (side.isVertical() ? existingRec.x : existingRec.y);
        if (alignmentSide == Directions.E) {
            dynamicCoord += existingRec.width - placeableBounds.width;
        } else if (alignmentSide == Directions.S) {
            dynamicCoord += existingRec.height - placeableBounds.height;
        }
        int x, y;
        if (side.isVertical()) {
            x = dynamicCoord;
            y = staticCoord;
        } else {
            x = staticCoord;
            y = dynamicCoord;
        }
        return placeable.place(builder, x, y);
    }

    public StepAwayFromFromSideMarginAlignShift shift(int shift) {
        return new StepAwayFromFromSideMarginAlignShift(pointer, side, margin, alignmentSide, shift);
    }


}
