package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.FiniteCellSet;

import java.awt.*;

import static java.util.Objects.requireNonNull;


public class DrawingCellSet {

    public static DrawingAlgorithm<? super FiniteCellSet> withColor(Color color) {
        requireNonNull(color);
        return (what, canvas) -> {
            for (Cell cell : what) {
                canvas.drawCell(cell, color);
            }
        };
    }
}
