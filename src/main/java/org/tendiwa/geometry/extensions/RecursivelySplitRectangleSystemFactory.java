package org.tendiwa.geometry.extensions;

import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.RectangleSystem;

import java.util.HashSet;
import java.util.Set;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

/**
 * A factory that creates RectangleSystem by taking one rectangle and recursively splitting it.
 */
public class RecursivelySplitRectangleSystemFactory {

	public static RectangleSystem create(
		int startX,
		int startY,
		int width,
		int height,
		int minRectangleWidth,
		int borderWidth
	) {
		RectangleSystem rs = new RectangleSystem(borderWidth);
		// If a Rectangle is less than this by a certain dimension, it won't be
		// split by that dimension
		int splittableRecSizeLimit = minRectangleWidth * 2 + borderWidth + 1;
		rs.addRectangle(rectangle(startX, startY, width, height));
		Chance ch = new Chance(50);
		boolean noMoreRectangles = false;
		// Randomly split area into content, saving the resulting graph
		while (!noMoreRectangles) {
			noMoreRectangles = true;
			// Cloned, because splitting rectangles changes contents.
			Set<Rectangle> rectangles = new HashSet<>(rs.getRectangles());
			for (Rectangle r : rectangles) {
				if (r.width() > splittableRecSizeLimit && r.height() > splittableRecSizeLimit) {
					noMoreRectangles = false;
					randomlySplitRectangle(r, ch.roll() ? Orientation.VERTICAL
						: Orientation.HORIZONTAL, rs, minRectangleWidth);
				} else if (r.width() > splittableRecSizeLimit) {
					noMoreRectangles = false;
					randomlySplitRectangle(r, Orientation.VERTICAL, rs, minRectangleWidth);
				} else if (r.height() > splittableRecSizeLimit) {
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
	private static void randomlySplitRectangle(
		Rectangle r,
		Orientation orientation,
		RectangleSystem rs,
		int minRectangleWidth
	) {
		if (orientation.isHorizontal()) {
			int y = Chance.rand(r.y() + minRectangleWidth, r.y() + r.height() - minRectangleWidth - rs
				.getBorderWidth());
			rs.splitRectangle(r, orientation, y - r.y(), false);
		} else {
			assert orientation.isVertical();
			int x = Chance.rand(
				r.x() + minRectangleWidth,
				r.x() + r.width() - minRectangleWidth - rs.getBorderWidth()
			);
			rs.splitRectangle(r, orientation, x - r.x(), false);
		}
	}
}
