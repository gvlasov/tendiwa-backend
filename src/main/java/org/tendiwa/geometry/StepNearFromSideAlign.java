package org.tendiwa.geometry;

import org.tendiwa.core.*;

public class StepNearFromSideAlign implements Placement {
    private final RectanglePointer pointer;
    private final CardinalDirection fromSide;
    private final CardinalDirection alignSide;

    StepNearFromSideAlign(RectanglePointer pointer, CardinalDirection fromSide, CardinalDirection alignSide) {
        this.pointer = pointer;
        this.fromSide = fromSide;
        this.alignSide = alignSide;
    }

    @Override
    public Rectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
        return shift(0).placeIn(placeable, builder);
    }

    public Placement shift(final int shift) {
        return new Placement() {
            @Override
            public Rectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
                Rectangle placeableBounds = placeable.getBounds();
                Rectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
                int staticCoord = existingRec.getStaticCoordOfSide(fromSide) + (builder.rs.getBorderWidth() + 1) * fromSide.getGrowing();
                if (fromSide == Directions.N) {
                    staticCoord -= placeableBounds.getHeight();
                } else if (fromSide == Directions.W) {
                    staticCoord -= placeableBounds.getWidth() - 1;
                }
                int dynamicCoord = (fromSide.isVertical() ? existingRec.getX() : existingRec.getY()) + shift * alignSide.getGrowing();
                if (alignSide == Directions.E) {
                    dynamicCoord += existingRec.getWidth() - placeableBounds.getWidth();
                } else if (alignSide == Directions.S) {
                    dynamicCoord += existingRec.getHeight() - placeableBounds.getHeight();
                }
                int x, y;
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
