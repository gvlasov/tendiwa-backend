package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.BasicCell;

import java.awt.*;

@SuppressWarnings("unused")
public class DrawingBoundedCellBufferBorder {
	public static DrawingAlgorithm<BoundedCellSet> withColor(final Color color) {
		return (border, canvas) -> {
			DrawingAlgorithm<BasicCell> how = DrawingCell.withColor(color);
			for (BasicCell cell : border.getBounds().getCells()) {
				if (border.contains(cell.x, cell.y)) {
					canvas.draw(cell, how);
				}
			}
		};
	}
}
