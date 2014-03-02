package org.tendiwa.drawing;

import org.tendiwa.geometry.Cell;

import java.awt.*;
import java.awt.geom.Ellipse2D;

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

public static DrawingAlgorithm<Cell> withColorAndSize(final Color color, final double diameter) {
	return new DrawingAlgorithm<Cell>() {
		@Override
		public void draw(Cell shape) {
			double halfDiameter = diameter / 2;
			fillShape(new Ellipse2D.Double(shape.x - halfDiameter, shape.y - halfDiameter, diameter, diameter), color);
		}
	};
}
}
