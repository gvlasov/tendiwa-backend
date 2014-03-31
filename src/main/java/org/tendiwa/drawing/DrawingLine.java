package org.tendiwa.drawing;

import org.tendiwa.geometry.Line2D;

import java.awt.*;

public class DrawingLine {
    public static DrawingAlgorithm<Line2D> withColor(final Color color) {
        return new DrawingAlgorithm<Line2D>() {
            @Override
            public void draw(Line2D shape) {
                drawShape(
                        new java.awt.geom.Line2D.Double(
                                shape.start.x,
                                shape.start.y,
                                shape.end.x,
                                shape.end.y
                        ),
                        color
                );
            }
        };
    }
}
