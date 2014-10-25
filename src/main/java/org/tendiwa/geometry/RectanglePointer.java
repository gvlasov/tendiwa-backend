package org.tendiwa.geometry;

/**
 * Used as an argument to some DSL chaining methods to point to a certain rectangle that was already placed.
 */
public enum RectanglePointer {
	/**
	 * The first rectangle placed.
	 */
	FIRST_RECTANGLE,
	/**
	 * The most recently placed rectangle.
	 */
	LAST_RECTANGLE,
	/**
	 * A rectangle after placing which {@link org.tendiwa.geometry.RectangleSystemBuilder#rememberRectangle()} was
	 * called in
	 * method chain.
	 */
	REMEMBERED_RECTANGLE,
	/**
	 * A bounding rectangle of the most recently placed Placeable
	 *
	 * @see Placeable#getBounds()
	 */
	LAST_BOUNDING_REC,
	/**
	 * A bounding rectagle of the Placeable after placing which {@link org.tendiwa.geometry.RectangleSystemBuilder#rememberBoundingRec()}
	 * was called in method chain.
	 */
	REMEMBERED_BOUNDING_REC,

	/**
	 * All rectangles that matched a criteria in search methods, i.e. {@link org.tendiwa.core.WorldRectangleBuilder#findAllRectangles(org.tendiwa.core.FindCriteria)}
	 */
	FOUND_RECTANGLES;
}
