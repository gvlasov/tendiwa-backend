package org.tendiwa.drawing;

import org.tendiwa.geometry.BoundedCellBufferBorder;
import org.tendiwa.geometry.Cell;

import java.awt.*;

@SuppressWarnings("unused")
public class DrawingBoundedCellBufferBorder {
    public static DrawingAlgorithm<BoundedCellBufferBorder> withColor(final Color color) {
        return (border, canvas) -> {
            DrawingAlgorithm<Cell> how = DrawingCell.withColor(color);
            for (Cell cell : border.getBounds().getCells()) {
                if (border.isBufferBorder(cell.x, cell.y)) {
                    canvas.draw(cell, how);
                }
            }
        };
    }
}
