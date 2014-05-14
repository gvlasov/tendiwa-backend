package org.tendiwa.drawing;

import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Cell;

import java.awt.*;

public class DrawingCellSet {

    public static DrawingAlgorithm<? super BoundedCellSet> withColor(Color color) {
        return (what, canvas) -> {
            for (Cell cell : what.toList()) {
                canvas.drawCell(cell, color);
            }
        };
    }
}
