package tendiwa.geometry;

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
    public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
        return shift(0).placeIn(placeable, builder);
    }

    public Placement shift(int shift) {
        return new Placement() {
            @Override
            public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
                EnhancedRectangle placeableBounds = placeable.getBounds();
                EnhancedRectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
                int staticCoord = existingRec.getStaticCoordOfSide(fromSide) + (builder.rs.borderWidth + 1) * fromSide.getGrowing();
                if (fromSide == Directions.N) {
                    staticCoord -= placeableBounds.height;
                } else if (fromSide == Directions.W) {
                    staticCoord -= placeableBounds.width - 1;
                }
                int dynamicCoord = (fromSide.isVertical() ? existingRec.x : existingRec.y) + shift * alignSide.getGrowing();
                if (alignSide == Directions.E) {
                    dynamicCoord += existingRec.width - placeableBounds.width;
                } else if (alignSide == Directions.S) {
                    dynamicCoord += existingRec.height - placeableBounds.height;
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
