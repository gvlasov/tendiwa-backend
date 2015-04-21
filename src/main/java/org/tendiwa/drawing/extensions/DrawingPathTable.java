package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.pathfinding.dijkstra.PathTable;

import java.awt.*;

@SuppressWarnings("unused")
public class DrawingPathTable {
	public static DrawingAlgorithm<PathTable> withColor(final Color color) {
		return (table, canvas) -> {
			DrawingAlgorithm<BasicCell> how = DrawingCell.withColor(Color.RED);
			for (BasicCell cell : table) {
				canvas.draw(cell, how);
			}
		};
	}
}
