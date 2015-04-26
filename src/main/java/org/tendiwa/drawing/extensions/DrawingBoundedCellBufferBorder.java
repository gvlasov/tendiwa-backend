package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.BasicCell;

import java.awt.*;

@SuppressWarnings("unused")
public class DrawingBoundedCellBufferBorder {
	public static DrawingAlgorithm<BoundedCellSet> withColor(final Color color) {
		return (border, canvas) -> {
			canvas.drawAll(
				border.getBounds()
					.getCells()
					.stream()
					.filter(border::contains),
				cell -> new DrawableCell(cell, color)
			);
		};
	}
}
