package org.tendiwa.core;

public interface WorldDrawer {
	/**
	 * Divides a rectangle of {@link World} into rectangles of {@link Location}s, assigns attributes to them and places
	 * paths between them.
	 *
	 * @param builder
	 * 	A new clear RectangleBuilder.
	 * @param width
	 * 	Bournds of World rectangle in cells.
	 * @param height
	 * 	Bounds of World rectangle in cells.
	 */
	void drawWorld(WorldRectangleBuilder builder, int width, int height);
}
