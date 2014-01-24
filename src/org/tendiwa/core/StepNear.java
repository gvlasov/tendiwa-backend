package org.tendiwa.core;

public class StepNear {
    private final RectanglePointer pointer;

    StepNear(RectanglePointer pointer) {
        this.pointer = pointer;
    }

    public StepNearFromSide fromSide(CardinalDirection side) {
        return new StepNearFromSide(pointer, side);
    }

    public Placement fromCorner(final OrdinalDirection corner) {
        return new Placement() {
            @Override
            public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
                EnhancedRectangle placeableBounds = placeable.getBounds();
                EnhancedRectangle existingRec = builder.getRectangleByPointer(pointer).getBounds();
                int x, y;
                switch (corner) {
                    case NE:
                        x = existingRec.getX() + existingRec.getWidth() + builder.rs.borderWidth;
                        y = existingRec.getY() - placeableBounds.getHeight() - builder.rs.borderWidth;
                        break;
                    case NW:
                        x = existingRec.getX() - placeableBounds.getWidth() - builder.rs.borderWidth;
                        y = existingRec.getY() - placeableBounds.getHeight() - builder.rs.borderWidth;
                        break;
                    case SE:
                        x = existingRec.getX() + existingRec.getWidth() + builder.rs.borderWidth;
                        y = existingRec.getY() + existingRec.getHeight() + builder.rs.borderWidth;
                        break;
                    case SW:
                        y = existingRec.getY() + existingRec.getHeight() + builder.rs.borderWidth;
                        x = existingRec.getX() - placeableBounds.getWidth() - builder.rs.borderWidth;
                        break;
                    default:
                        throw new Error();
                }
                return placeable.place(builder, x, y);
            }
        };
    }
}
