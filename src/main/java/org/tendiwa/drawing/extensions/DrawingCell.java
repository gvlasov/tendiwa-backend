package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.BasicCell;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public final class DrawingCell {
	private DrawingCell() {

	}

	/**
	 * Draws a Cell the same way as {@link org.tendiwa.drawing.TestCanvas#drawCell(int, int, java.awt.Color)}
	 *
	 * @param color
	 * 	Color of a cell.
	 */
	public static DrawingAlgorithm<BasicCell> withColor(final Color color) {
		return (point, canvas) -> canvas.drawCell(point.x(), point.y(), color);
	}

	/**
	 * Draws circle with center at a Cell.
	 *
	 * @param color
	 * 	Color of a circle.
	 * @param diameter
	 * 	Diameter of a cicrle.
	 */
	public static DrawingAlgorithm<BasicCell> withColorAndSize(final Color color, final double diameter) {
		return (shape, canvas) -> {
			double halfDiameter = diameter / 2;
			canvas.fillShape(
				new Ellipse2D.Double(
					shape.x - halfDiameter,
					shape.y - halfDiameter,
					diameter,
					diameter
				),
				color
			);
		};
	}
}
