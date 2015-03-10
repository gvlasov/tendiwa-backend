package org.tendiwa.geometry;

public interface RectangularHull {
	double minX();

	double maxX();

	double minY();

	double maxY();

	/**
	 * <a href="http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other">
	 * StackOverflow: Determine if two rectangles overlap each other</a>
	 *
	 * @param hull
	 * 	Another hull.
	 * @return true if hulls strictly intersect (area of intersection is greater than 0), false otherwise.
	 */
	default boolean intersectsHull(RectangularHull hull) {
		return minX() < hull.maxX() && maxX() > hull.minX() && minY() < hull.maxY() && maxY() > hull.minY();
	}
}
