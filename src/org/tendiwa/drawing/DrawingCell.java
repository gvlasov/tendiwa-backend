package org.tendiwa.drawing;

import org.tendiwa.geometry.Cell;

import java.awt.Color;

public final class DrawingCell {
	private DrawingCell() {

	}
	public static DrawingAlgorithm<Cell> withColor(final Color color) {
		return new DrawingAlgorithm<Cell>() {
			@Override
			public void draw(Cell point) {
				drawPoint(point.getX(), point.getY(), color);
			}
		};
	}
}
