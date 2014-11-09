package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.geometry.Rectangle;

import java.util.Arrays;

/**
 * The mutable counterpart of {@link org.tendiwa.geometry.extensions.polygonRasterization.RasterizationResult}.
 *
 * @see org.tendiwa.geometry.extensions.polygonRasterization.PolygonRasterizer#rasterizeToMutable(java.util.List) to
 * construct this object.
 */
public class MutableRasterizationResult {
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final boolean[][] bitmap;

	MutableRasterizationResult(int minX, int minY, boolean[][] bitmap) {
		this.x = minX;
		this.y = minY;
		this.width = (bitmap.length == 0) ? 0 : bitmap[0].length;
		this.height = bitmap.length;
		this.bitmap = bitmap;
	}

	/**
	 * Fills a rectangular area within {@link #bitmap} with obstacle cells. Coordinates of filled cells are
	 * relative to {@link #x} and {@link #y}.
	 *
	 * @param r
	 * 	A rectangle to fill.
	 */
	public void excludeRectangle(Rectangle r) {
		int startX = r.x - x;
		int endX = r.x + r.width - x;
		int endY = r.y - this.y + r.height;
		for (int row = r.y - this.y; row < endY; row++) {
			Arrays.fill(
				bitmap[row],
				startX,
				endX,
				false
			);
		}
	}
}
