package org.tendiwa.geometry.extensions.daveedvMaxRec;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import lombok.Lazy;
import org.tendiwa.geometry.Mutable2DCellSet;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.polygonRasterization.RasterizedPolygon;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RectangleCoverage extends ForwardingList<Rectangle> {
	private final RasterizedPolygon polygon;
	private final int minimumArea;

	/**
	 * @param polygon
	 * @param minimumArea
	 * 	Rectangles will be produced until one's area is less than {@code minimumArea}.
	 */
	public RectangleCoverage(RasterizedPolygon polygon, int minimumArea) {
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
	private ImmutableList<Rectangle> rectangles() {
		Optional<Rectangle> r;
		Mutable2DCellSet rasterCopy = new Mutable2DCellSet(polygon);
		ImmutableList.Builder<Rectangle> builder = ImmutableList.builder();
		while (true) {
			r = rasterCopy.maximalRectangle();
			if (!r.isPresent() || r.get().area() < minimumArea) {
				break;
			}
			Rectangle bounds = rasterCopy.getBounds();
			Rectangle maxRecAbsolute = r.get().translate(bounds.x(), bounds.y());
			builder.add(maxRecAbsolute);
			rasterCopy.excludeRectangle(maxRecAbsolute);
		}
		return builder.build();
	}

	@Lazy
	@Override
	protected final List<Rectangle> delegate() {
		return rectangles();
	}
}
