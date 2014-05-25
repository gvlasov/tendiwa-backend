package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Cell;
import org.tendiwa.pathfinding.dijkstra.PathTable;

import java.awt.*;

@SuppressWarnings("unused")
public class DrawingPathTable {
    public static DrawingAlgorithm<PathTable> withColor(final Color color) {
        return (table, canvas) -> {
            DrawingAlgorithm<Cell> how = DrawingCell.withColor(Color.RED);
            for (Cell cell : table) {
                canvas.draw(cell, how);
            }
        };
    }
}
