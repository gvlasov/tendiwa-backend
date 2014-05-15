package org.tendiwa.drawing;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.CellSegment;

import java.awt.Color;

public class DrawingCellSegment {
    public static DrawingAlgorithm<CellSegment> withColor(Color color) {
        return (what, canvas) -> {
            for (Cell cell : what) {
                canvas.drawCell(cell, color);
            }
        };
    }
}
