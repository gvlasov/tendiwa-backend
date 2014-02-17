package org.tendiwa.geometry;


import org.tendiwa.core.*;

import java.awt.*;

public class RectangleNearFromSideInMiddlePlacer implements Placement {
    private final RectanglePointer pointer;
    private final RelativeCardinalDirection side;

    RectangleNearFromSideInMiddlePlacer(Rectangle rectangle, RectanglePointer pointer, RelativeCardinalDirection side) {
        this.pointer = pointer;
        this.side = side;
    }

    @Override
    public EnhancedRectangle placeIn(Placeable placeable, RectangleSystemBuilder builder) {
        builder.getRectangleByPointer(pointer);
        throw new UnsupportedOperationException();
    }
}
