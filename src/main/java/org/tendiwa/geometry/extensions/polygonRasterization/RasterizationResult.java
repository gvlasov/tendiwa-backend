package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.*;

import java.util.function.Consumer;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

/**
 * Represents the result of polygon rasterization: a grid within a rectangular area with cells that either belong
 * to polygon's area or not.
 *
 * @see org.tendiwa.geometry.extensions.polygonRasterization.PolygonRasterizer#rasterizeToCellSet(java.util.List) to
 * construct this object.
 */
public class RasterizationResult implements BoundedCellSet {
	private final Rectangle bounds;
	/**
	 * Array of obstacles. {@code false} means an obstacle, {@code true} means no obstacle. First index is
	 * y-coordinate, second index is x-coordinate.
	 * <p>
	 * Bitmap itself has to be public (and hence <b>mutable</b>),
	 * because otherwise we would need to defensively copy it each time the result is
	 * passed to another algorithm, which is time-consuming.
	 */
	private final boolean[][] bitmap;

	/**
	 * @param x
	 * 	Least x-coordinate.
	 * @param y
	 * 	Least y-coordinate.
	 * @param bitmap
	 * 	First index is y-coordinate, second index is x-coordinate (for performance reasons, see
	 * 	{@link java.util.Arrays#fill(boolean[], boolean)}
	 */
	RasterizationResult(int x, int y, boolean[][] bitmap) {
		this.bounds = rectangle(
			x,
			y,
			(bitmap.length == 0) ? 0 : bitmap[0].length,
			bitmap.length
		);
		this.bitmap = bitmap;
	}

	public boolean get(int x, int y) {
		return bitmap[y][x];
	}


	@Override
	public boolean contains(int x, int y) {
		return bounds.contains(x, y) && bitmap[y - bounds.y()][x - bounds.x()];
	}

	@Override
	public void forEach(Consumer<? super Cell> action) {
		int maxX = bounds.maxX();
		int maxY = bounds.maxY();
		for (int i = bounds.x(); i < maxX; i++) {
			for (int j = bounds.y(); j < maxY; j++) {
				if (bitmap[j - bounds.y()][i - bounds.x()]) {
					action.accept(new BasicCell(i, j));
				}
			}
		}
	}

	@Override
	public void forEach(CellConsumer action) {
		int maxX = bounds.maxX();
		int maxY = bounds.maxY();
		for (int i = bounds.x(); i < maxX; i++) {
			for (int j = bounds.y(); j < maxY; j++) {
				if (bitmap[j - bounds.y()][i - bounds.x()]) {
					action.consume(i, j);
				}
			}
		}
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}
}
