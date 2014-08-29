package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.settlements.RectangleWithNeighbors;
import sun.java2d.loops.DrawRect;

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
			canvas.draw(shape.rectangle, DrawingRectangle.withColorAndBorder(rectangle, rectangle.darker()));
			for (Rectangle neighbor : shape.neighbors) {
				canvas.draw(neighbor, DrawingRectangle.withColorAndBorder(neighbors, neighbors.darker()));
			}
		};
	}
}
