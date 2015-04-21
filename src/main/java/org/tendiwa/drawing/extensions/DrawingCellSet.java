package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;

import static java.util.Objects.requireNonNull;


public final class DrawingCellSet {

	public static DrawingAlgorithm<? super FiniteCellSet> withColor(Color color) {
		requireNonNull(color);
		return (what, canvas) -> {
			for (BasicCell cell : what) {
				canvas.drawCell(cell, color);
			}
		};
	}

	/**
	 * For each point on {@link org.tendiwa.drawing.DrawableInto}, draw {@code color} in if if that point is in the
	 * {@link CellSet} being drawn.
	 *
	 * @param color
	 * 	A color to draw with.
	 * @return A drawing algorithm that can draw a cell set with {@code color}.
	 */
	public static DrawingAlgorithm<? super CellSet> onWholeCanvasWithColor(Color color) {
		requireNonNull(color);
		return (what, canvas) -> {
			for (BasicCell cell : new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight())) {
				if (what.contains(cell)) {
					canvas.drawCell(cell, color);
				}
			}
		};
	}
}
