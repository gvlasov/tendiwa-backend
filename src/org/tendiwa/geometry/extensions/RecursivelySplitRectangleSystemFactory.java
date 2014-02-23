package org.tendiwa.geometry.extensions;

import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.RectangleSystem;

import java.util.HashSet;
import java.util.Set;

/**
 * A factory that creates RectangleSystem by taking one rectangle and recursively splitting it.
 */
public class RecursivelySplitRectangleSystemFactory {

public static RectangleSystem create(int startX, int startY, int width, int height, int minRectangleWidth, int borderWidth) {
	RectangleSystem rs = new RectangleSystem(borderWidth);
	// If a Rectangle is less than this by a certain dimension, it won't be
	// split by that dimension
	int splitableRecSizeLimit = minRectangleWidth * 2 + borderWidth + 1;
	rs.addRectangle(new Rectangle(startX, startY, width, height));
	Chance ch = new Chance(50);
	boolean noMoreRectangles = false;
	// Randomly split area into content, saving the resulting graph
	while (!noMoreRectangles) {
		noMoreRectangles = true;
		// Cloned, because splitting rectangles changes contents.
		Set<Rectangle> rectangles = new HashSet<>(rs.getRectangles());
		for (Rectangle r : rectangles) {
			if (r.getWidth() > splitableRecSizeLimit && r.getHeight() > splitableRecSizeLimit) {
				noMoreRectangles = false;
				randomlySplitRectangle(r, ch.roll() ? Orientation.VERTICAL
					: Orientation.HORIZONTAL, rs, minRectangleWidth);
			} else if (r.getWidth() > splitableRecSizeLimit) {
				noMoreRectangles = false;
				randomlySplitRectangle(r, Orientation.VERTICAL, rs, minRectangleWidth);
			} else if (r.getHeight() > splitableRecSizeLimit) {
				noMoreRectangles = false;
				randomlySplitRectangle(r, Orientation.HORIZONTAL, rs, minRectangleWidth);
			}
		}
	}
	return rs;
}

/**
 * Splits a Rectangle into two EnhancedRectangles, randomly choosing the line of splitting.
 *
 * @param r
 * 	A Rectangle to split.
 * @param orientation
 * 	DirectionToBERemoved of splitting.
 */
private static void randomlySplitRectangle(Rectangle r, Orientation orientation, RectangleSystem rs, int minRectangleWidth) {
	if (orientation.isHorizontal()) {
		int y = Chance.rand(r.getY() + minRectangleWidth, r.getY() + r.getHeight() - minRectangleWidth - rs.getBorderWidth());
		rs.splitRectangle(r, orientation, y - r.getY(), false);
	} else {
		assert orientation.isVertical();
		int x = Chance.rand(r.getX() + minRectangleWidth, r.getX() + r.getWidth() - minRectangleWidth - rs.getBorderWidth());
		rs.splitRectangle(r, orientation, x - r.getX(), false);
	}
}
}
