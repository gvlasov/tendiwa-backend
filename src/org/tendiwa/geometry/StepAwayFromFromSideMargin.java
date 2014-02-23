package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepAwayFromFromSideMargin {
    private final RectanglePointer pointer;
    private final CardinalDirection side;
    private final int margin;

    StepAwayFromFromSideMargin(RectanglePointer pointer, CardinalDirection side, int margin) {
        if (margin < 0) {
            throw new IllegalArgumentException("margin must be 0 or greater");
        }
        if (side == null) {
            throw new NullPointerException("side can't be null");
        }
        if (pointer == null) {
            throw new NullPointerException("pointer can't be null");
        }
        this.pointer = pointer;
        this.side = side;
        this.margin = margin;
    }

    public StepAwayFromFromSideMarginAlign align(CardinalDirection alignmentSide) {
        if (alignmentSide == null) {
            throw new NullPointerException("argument can't be null");
        }
        return new StepAwayFromFromSideMarginAlign(pointer, side, margin, alignmentSide);
    }

    public Placement inMiddle() {
        return new Placement() {
            @Override
            public Rectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
                Rectangle placeableBounds = placeable.getBounds();
                Rectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
                int staticCoord = existingRec.getStaticCoordOfSide(side) + (builder.rs.getBorderWidth() + 1 + margin) * side.getGrowing();
                if (side == Directions.N) {
                    staticCoord -= placeableBounds.getHeight();
                } else if (side == Directions.W) {
	                staticCoord -= placeableBounds.getWidth();
                }
	            int dynamicCoord = (side.isVertical() ? existingRec.getX() : existingRec.getY()) + (existingRec.getDimensionBySide(side) - placeableBounds.getDimensionBySide(side)) / 2;
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
        };
    }

}
