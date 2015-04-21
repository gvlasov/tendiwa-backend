package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

final class RectangleCenteredAt extends Rectangle_Wr {
	public RectangleCenteredAt(Cell cell, int width, int height) {
		super(rectangleByCenterPoint(cell, width, height));
	}

	/**
	 * Creates a new rectangle whose center is the given cell, with given width and height. If the rectangle created
	 * has
	 * even width/height, the exact center coordinate will be randomized between two possible coordinates.
	 *
	 * @param cell
	 * @param width
	 * @param height
	 * @return
	 */
	public static Rectangle rectangleByCenterPoint(Cell cell, int width, int height) {
		return new BasicRectangle(
			cell.x() - width / 2,
			cell.y() - height / 2,
			width,
			height);
	}
}
