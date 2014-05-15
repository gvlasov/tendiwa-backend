package org.tendiwa.drawing;

import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.FiniteCellSet;

import java.awt.*;

public class DrawingCellSet {

    public static DrawingAlgorithm<? super FiniteCellSet> withColor(Color color) {
        return (what, canvas) -> {
            for (Cell cell : what) {
                canvas.drawCell(cell, color);
            }
        };
    }
}
