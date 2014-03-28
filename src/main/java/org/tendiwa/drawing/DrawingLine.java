package org.tendiwa.drawing;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Line2D;

import java.awt.*;

public class DrawingLine {
    public static DrawingAlgorithm<Line2D> withColor(final Color color) {
        return new DrawingAlgorithm<Line2D>() {
            @Override
            public void draw(Line2D shape) {
                this.drawLine(
                        new Cell(
                                (int) shape.start.x,
                                (int) shape.start.y
                        ),
                        new Cell(
                                (int) shape.end.x,
                                (int) shape.end.y
                        ),
                        color
                );
            }
        };
    }
}
