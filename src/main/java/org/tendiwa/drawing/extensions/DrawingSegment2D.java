package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Segment2D;

import java.awt.*;

public class DrawingSegment2D {
    public static DrawingAlgorithm<Segment2D> withColor(final Color color) {
        return (shape, canvas) ->
                canvas.drawShape(
                        new java.awt.geom.Line2D.Double(
                                shape.start.x,
                                shape.start.y,
                                shape.end.x,
                                shape.end.y
                        ),
                        color
                );
    }
}
