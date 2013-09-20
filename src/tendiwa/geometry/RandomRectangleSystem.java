package tendiwa.geometry;

import tendiwa.core.meta.Chance;

import java.util.HashSet;
import java.util.Set;

/**
 * A RectangleSystem that is built randomly splitting an initial rectangle of a particular size.
 */
public class RandomRectangleSystem extends RectangleSystem {
public int minRectangleWidth;

public RandomRectangleSystem(int startX, int startY, int width, int height, int minRectangleWidth, int borderWidth) {
	super(borderWidth);
	this.minRectangleWidth = minRectangleWidth;
	// If a Rectangle is less than this by a certain dimension, it won't be
	// split by that dimension
	int splitableRecSizeLimit = minRectangleWidth * 2 + borderWidth + 1;
	addRectangle(new EnhancedRectangle(startX, startY, width, height));
	Chance ch = new Chance(50);
	boolean noMoreRectangles = false;
	// Randomly split area into content, saving the resulting graph
	while (!noMoreRectangles) {
		noMoreRectangles = true;
		// Cloned, because splitting rectangles changes contents.
		Set<EnhancedRectangle> rectangles = new HashSet<>(content);
		for (EnhancedRectangle r : rectangles) {
			if (r.width > splitableRecSizeLimit && r.height > splitableRecSizeLimit) {
				noMoreRectangles = false;
				randomlySplitRectangle(r, ch.roll() ? Orientation.VERTICAL
						: Orientation.HORIZONTAL);
			} else if (r.width > splitableRecSizeLimit) {
				noMoreRectangles = false;
				randomlySplitRectangle(r, Orientation.VERTICAL);
			} else if (r.height > splitableRecSizeLimit) {
				noMoreRectangles = false;
				randomlySplitRectangle(r, Orientation.HORIZONTAL);
			}
		}
	}
}

/**
 * Splits a EnhancedRectangle into two EnhancedRectangles, randomly choosing the line of splitting.
 *
 * @param r
 * 		A EnhancedRectangle to split.
 * @param orientation
 * 		DirectionToBERemoved of splitting.
 */
private void randomlySplitRectangle(EnhancedRectangle r, Orientation orientation) {
	if (orientation.isHorizontal()) {
		int y = Chance.rand(r.y + minRectangleWidth, r.y + r.height - minRectangleWidth - borderWidth);
		splitRectangle(r, orientation, y - r.y, false);
	} else {
		assert orientation.isVertical();
		int x = Chance.rand(r.x + minRectangleWidth, r.x + r.width - minRectangleWidth - borderWidth);
		splitRectangle(r, orientation, x - r.x, false);
	}
}
}
