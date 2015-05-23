package org.tendiwa.geometry.extensions.daveedvMaxRec;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Mutable2DCellSet;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.polygonRasterization.MutableRasterizedPolygon;

import java.util.Optional;

public class RectangleCoverage {
	private final Polygon polygon;
	private final int minimumArea;

	/**
	 * @param polygon
	 * 	A polygon to rasterize
	 * @param minimumArea
	 * 	Rectangles will be produced until one's area is less than {@code minimumArea}.
	 */
	public RectangleCoverage(Polygon polygon, int minimumArea) {
		this.polygon = polygon;
		this.minimumArea = minimumArea;
	}

	/**
	 * Repeats finding the largest polygon and excluding its cells from {@code rasterizedPolygon} until there is no
	 * more polygon to find whose area is greater than {@code minimumArea}.
	 *
	 * @return List of rectangles within {@code rasterizedPolygon}. Rectangle with greater index will have lesser or
	 * equal area to the previous rectangle in list. No rectangle in list has area greater than {@code minimumArea}.
	 */
	public ImmutableList<Rectangle> rectangles() {
		Mutable2DCellSet rasterized = new MutableRasterizedPolygon(polygon);
		Optional<Rectangle> r;
		ImmutableList.Builder<Rectangle> builder = ImmutableList.builder();
		while (true) {
			r = rasterized.maximalRectangle();
			if (!r.isPresent() || r.get().area() < minimumArea) {
				break;
			}
			Rectangle bounds = rasterized.getBounds();
			Rectangle maxRecAbsolute = r.get().translate(bounds.x(), bounds.y());
			builder.add(maxRecAbsolute);
			rasterized.excludeRectangle(maxRecAbsolute);
		}
		return builder.build();
	}
}
