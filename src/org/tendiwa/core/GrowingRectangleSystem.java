package org.tendiwa.core;

import java.awt.*;

/**
 * A RectangleSystem that is constructed by setting an "initial" rectangle and
 * then adding new rectangles adjacent to first the existing one, and later to
 * the "grown" ones.
 * 
 * @author suseika
 * 
 */
public class GrowingRectangleSystem extends RectangleSystem {

	public GrowingRectangleSystem(int borderWidth) {
		super(borderWidth);
	}

	public GrowingRectangleSystem(int borderWidth, Rectangle r) {
		this(borderWidth);
		addRectangle(new EnhancedRectangle(r));
	}

	/**
	 * From certain side of a RectangleArea existing in this system, create
	 * another rectangle of given size. Then shift the created rectangle to a
	 * certain amount of cells.
	 * 
	 * @param side
	 *            Only cardinal sides.
	 * @param width
	 *            Width of created rectangle.
	 * @param height
	 *            Height of created rectangle.
	 * @param offset
	 *            If side is N or S, positive offset moves the rectangle to the
	 *            east, and negative — to the west. If side is W or E, positive
	 *            offset moves. the rectangle to the south, and negative — to
	 *            the north.
	 */
	public void grow(Rectangle r, CardinalDirection side, int width, int height, int offset) {
		addRectangle(create(r, side, width, height, offset));
	}
	/**
	 * Creates, but doesn't placeIn a new rectangle from a side of an existing
	 * rectangle.
	 * 
	 * @param r
	 *            Rectangle from whose side a new rectangle will be created.
	 * @param side
	 *            Side of rectangle {@code r} on which a new rectangle will be
	 *            created.
	 * @param width
	 *            Width of a new rectangle.
	 * @param height
	 *            Height of a new rectangle.
	 * @param offset
	 * @return New rectangle.
	 */
	protected EnhancedRectangle create(Rectangle r, CardinalDirection side, int width, int height, int offset) {
		int startX = 0;
		int startY = 0;
		switch (side) {
			case N:
				startX = r.x + offset;
				startY = r.y - borderWidth - height;
				break;
			case E:
				startX = r.x + r.width + borderWidth;
				startY = r.y + offset;
				break;
			case S:
				startX = r.x + offset;
				startY = r.y + r.height + borderWidth;
				break;
			case W:
			default:
				startX = r.x - borderWidth - width;
				startY = r.y + offset;
				break;
		}
		return new EnhancedRectangle(startX, startY, width, height);
	}
}
