package org.tendiwa.drawing;

import java.awt.Color;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Segment;

public class DrawingSegment {
    private DrawingSegment() {
    }

    public static DrawingAlgorithm<Segment> withColor(final Color color) {
        return (segment, canvas) -> {
            for (Cell point : segment) {
                canvas.drawCell(point.getX(), point.getY(), color);
            }

        };
    }

}
