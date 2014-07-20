package org.tendiwa.geometry.extensions.axisAlignedRecsFromPolygons;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle;

import java.util.List;

public interface AxisAlignedRectangleLocator {
	/**
	 * Locates the largest axis-aligned rectangle inside a polygon.
	 *
	 * @param polygon
	 * 	A polygon to locate the largest axis-aligned rectangle on. This polygon should not be mutated by the
	 * 	implementation of the rectangle locator.
	 */
	public Rectangle locateOn(List<Point2D> polygon);
}
