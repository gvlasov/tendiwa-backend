package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepNearFromSide {

    private final RectanglePointer pointer;
    private final CardinalDirection fromSide;

    StepNearFromSide(RectanglePointer pointer, CardinalDirection side) {
        this.pointer = pointer;
        this.fromSide = side;
    }

    public StepNearFromSideAlign align(CardinalDirection endSide) {
        return new StepNearFromSideAlign(pointer, fromSide, endSide);
    }

    public Placement inMiddle() {
        return new Placement() {
            @Override
            public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
                EnhancedRectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
                EnhancedRectangle placeableBounds = placeable.getBounds();
                int staticCoord = existingRec.getStaticCoordOfSide(fromSide) + (builder.rs.getBorderWidth() + 1) * fromSide.getGrowing();
                int x, y;
	            int dynamicCoord = (fromSide.isVertical() ? existingRec.getX() : existingRec.getY()) + (existingRec.getDimensionBySide(fromSide) - placeableBounds.getDimensionBySide(fromSide)) / 2;
	            if (fromSide.isVertical()) {
                    x = dynamicCoord;
                    y = staticCoord;
                } else {
                    x = staticCoord;
                    y = dynamicCoord;
                }
                return placeable.place(builder, x, y);
            }
        };
    }
}
