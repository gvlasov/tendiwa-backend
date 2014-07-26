package org.tendiwa.geometry.extensions.daveedvMaxRec;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.PolygonRasterizer;

import java.util.Optional;

public class MaximalRectanlges {
	public static Optional<Rectangle> findLargestIn(PolygonRasterizer.Result rasterizedPolygon) {
		Optional<Rectangle> r = MaximalCellRectangleFinder.compute(rasterizedPolygon.bitmap);
		if (r.isPresent()) {
			return Optional.of(Recs.rectangleMovedFromOriginal(
				r.get(),
				rasterizedPolygon.x,
				rasterizedPolygon.y
			));
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Repeats finding the largest polygon and excluding its cells from {@code rasterizedPolygon} until there is no
	 * more polygon to find whose area is greater than {@code minimumArea}.
	 *
	 * @param rasterizedPolygon
	 * 	A bitmap to find rectangles in.
	 * @param minimumArea
	 * 	Rectangles will be produced until one's area is less than {@code minimumArea}.
	 * @return List of rectangles within {@code rasterizedPolygon}. Rectangle with greater index will have lesser or
	 * equal area to the previous rectangle in list. No rectangle in list has area greater than {@code minimumArea}.
	 */
	public static ImmutableList<Rectangle> searchUntilSmallEnoughMutatingBitmap(
		PolygonRasterizer.Result rasterizedPolygon,
		int minimumArea
	) {
		Optional<Rectangle> r;
		ImmutableList.Builder<Rectangle> builder = ImmutableList.builder();
		while (true) {
			r = MaximalCellRectangleFinder.compute(rasterizedPolygon.bitmap);
			if (!r.isPresent() || r.get().area() < minimumArea) {
				break;
			}
			Rectangle maxRecAbsolute = r.get().moveBy(rasterizedPolygon.x, rasterizedPolygon.y);
			builder.add(maxRecAbsolute);
			rasterizedPolygon.excludeRectangle(maxRecAbsolute);
		}
		return builder.build();
	}
}
