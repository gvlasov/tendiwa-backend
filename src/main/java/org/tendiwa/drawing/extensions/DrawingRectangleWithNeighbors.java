package org.tendiwa.drawing.extensions;

import org.tendiwa.demos.DrawableRectangle;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;

import java.awt.Color;

public class DrawingRectangleWithNeighbors {
	/**
	 * Draws the main rectangle and its neighbors with border. Border for both kinds of rectangle is selected with
	 * {@link java.awt.Color#darker()}.
	 *
	 * @param rectangle
	 * 	Color of main rectangle.
	 * @param neighbors
	 * 	Color of neighbors of the main rectangle.
	 * @return Drawing algorithm that draws rectangles with darker borders.
	 */
	public static DrawingAlgorithm<RectangleWithNeighbors> withColorAndDefaultBorder(Color rectangle, Color neighbors) {
		return (shape, canvas) -> {
			canvas.draw(
				new DrawableRectangle.Outlined(
					shape.rectangle,
					rectangle,
					rectangle.darker()
				)
			);
			canvas.drawAll(
				shape.neighbors,
				neighbor-> new DrawableRectangle.Outlined(
					neighbor,
					neighbors,
					neighbors.darker()
				)
			);
		};
	}
}
