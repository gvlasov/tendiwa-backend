package org.tendiwa.settlements;

import org.tendiwa.geometry.extensions.PolygonRasterizer;
import org.tendiwa.geometry.extensions.daveedvMaxRec.MaximalRectanlges;

import java.util.Set;
import java.util.stream.Collectors;

public class RecgangularBuildingLots {
	private RecgangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> findIn(CityGeometry cityGeometry) {
		Set<EnclosedBlock> encBlocks = cityGeometry
			.getBlocks()
			.stream()
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.flatMap(b -> b.subdivideLots(10, 10, 1).stream())
			.collect(Collectors.toSet());
		return encBlocks.stream()
			.map(lot -> PolygonRasterizer.rasterize(lot.toPolygon()))
			.map(rasterized -> MaximalRectanlges.searchUntilSmallEnoughMutatingBitmap(rasterized, 9))
			.filter(list -> !list.isEmpty())
			.map(list -> new RectangleWithNeighbors(list.get(0), list.subList(1, list.size())))
			.collect(Collectors.toSet());
	}
}
