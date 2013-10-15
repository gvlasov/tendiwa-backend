package tendiwa.geometry;

public class StepNear {
    private final RectanglePointer pointer;

    StepNear(RectanglePointer pointer) {
        this.pointer = pointer;
    }

    public StepNearFromSide fromSide(CardinalDirection side) {
        return new StepNearFromSide(pointer, side);
    }

    public Placement fromCorner(OrdinalDirection corner) {
        return new Placement() {
            @Override
            public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
                EnhancedRectangle placeableBounds = placeable.getBounds();
                EnhancedRectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
                int x, y;
                switch (corner) {
                    case NE:
                        x = existingRec.x + existingRec.width + builder.rs.borderWidth;
                        y = existingRec.y - placeableBounds.height - builder.rs.borderWidth;
                        break;
                    case NW:
                        x = existingRec.x - placeableBounds.width - builder.rs.borderWidth;
                        y = existingRec.y - placeableBounds.height - builder.rs.borderWidth;
                        break;
                    case SE:
                        x = existingRec.x + existingRec.width + builder.rs.borderWidth;
                        y = existingRec.y + existingRec.height + builder.rs.borderWidth;
                        break;
                    case SW:
                        y = existingRec.y + existingRec.height + builder.rs.borderWidth;
                        x = existingRec.x - placeableBounds.width - builder.rs.borderWidth;
                        break;
                    default:
                        throw new Error();
                }
                return placeable.place(builder, x, y);
            }
        };
    }
}
