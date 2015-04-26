package org.tendiwa.drawing.extensions;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.BasicCell;
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
