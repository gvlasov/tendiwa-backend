package tendiwa.geometry;

import java.awt.Rectangle;

import tendiwa.core.meta.Side;

public class GrowingRectangleSystem extends RectangleSystem {

	public GrowingRectangleSystem(int borderWidth) {
		super(borderWidth);
	}

	public GrowingRectangleSystem(int borderWidth, EnhancedRectangle r) {
		this(borderWidth);
		addRectangleArea(new RectangleArea(r));
	}

	/**
	 * From certain side of a RectangleArea existing in this system, creates
	 * another rectangle of given size. Then shifts the created rectangle to a
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
	public void grow(EnhancedRectangle r, Side side, int width, int height, int offset) {
		if (!side.isCardinal()) {
			throw new IllegalArgumentException("Only cardinal sides are allowed");
		}
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
			startX = r.x - borderWidth - width;
			startY = r.y + offset;
			break;
		default:
			break;
		}
		if (side == Side.N) {
			startX = r.x + offset;
			startY = r.y - borderWidth - height;
		} else if (side == Side.E) {
			startX = r.x + r.width - 1 + borderWidth;
			startY = r.y + offset;
		}
		addRectangleArea(new Rectangle(startX, startY, width, height));
	}
}
