package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.RectangleSystem;

import java.util.ArrayList;
import java.util.Collection;

public class ExpandRectanglesToBorder {
/**
 * Changes {@link RectangleSystem#borderWidth} of this RectangleSystem and expands all EnhancedRectangles by that amount
 * of cells to still be neighbors with their neighbors.
 *
 * @param depth
 * 	Difference between the old borderWidth and the desired borderWidth.
 */
public static RectangleSystem expandRectanglesToBorder(RectangleSystem rs, int depth) {
	if (rs.getBorderWidth() < depth * 2) {
		throw new Error(
			"border width " + rs.getBorderWidth() + " is too thin for expanding each rectangle by " + depth);
	}
	Collection<Rectangle> newRectangles = new ArrayList<>();
	for (Rectangle r : rs.getRectangles()) {
		newRectangles.add(new Rectangle(
			r.getX() - depth,
			r.getY() - depth,
			r.getWidth() + depth * 2,
			r.getHeight() + depth * 2
		));
	}
	RectangleSystem newRs = new RectangleSystem(rs.getBorderWidth() - depth * 2);
	for (Rectangle r : newRectangles) {
		newRs.addRectangle(r);
	}
	return newRs;
}
}
