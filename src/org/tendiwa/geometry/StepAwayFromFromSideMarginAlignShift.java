package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepAwayFromFromSideMarginAlignShift implements Placement {
    private final RectanglePointer pointer;
    private final CardinalDirection side;
    private final int margin;
    private final CardinalDirection alignmentSide;
    private final int shift;

    StepAwayFromFromSideMarginAlignShift(RectanglePointer pointer, CardinalDirection side, int margin, CardinalDirection alignmentSide, int shift) {
        this.pointer = pointer;
        this.side = side;
        this.margin = margin;
        this.alignmentSide = alignmentSide;
        this.shift = shift;
    }

    @Override
    public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
        EnhancedRectangle placeableBounds = placeable.getBounds();
        EnhancedRectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
        int staticCoord = existingRec.getStaticCoordOfSide(side) + (builder.rs.getBorderWidth() + 1 + margin) * side.getGrowing();
        if (side == Directions.N) {
            staticCoord -= placeableBounds.getHeight();
        } else if (side == Directions.W) {
            staticCoord -= placeableBounds.getWidth();
        }
        int dynamicCoord = (side.isVertical() ? existingRec.getX() : existingRec.getY()) + shift*alignmentSide.getGrowing();
        if (alignmentSide == Directions.E) {
            dynamicCoord += existingRec.getWidth() - placeableBounds.getWidth();
        } else if (alignmentSide == Directions.S) {
            dynamicCoord += existingRec.getHeight() - placeableBounds.getHeight();
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
}
