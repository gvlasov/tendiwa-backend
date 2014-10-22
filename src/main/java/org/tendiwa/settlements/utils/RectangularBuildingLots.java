package org.tendiwa.settlements.utils;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.geometry.extensions.PolygonRasterizer;
import org.tendiwa.geometry.extensions.daveedvMaxRec.MaximalRectanlges;
import org.tendiwa.settlements.EnclosedBlock;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.RoadsPlanarGraphModel;

import java.awt.Color;
import java.util.Set;
import java.util.stream.Collectors;

public final class RectangularBuildingLots {
	private RectangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> placeInside(RoadsPlanarGraphModel roadsPlanarGraphModel) {
		Set<EnclosedBlock> encBlocks = roadsPlanarGraphModel
			.getBlocks()
			.stream()
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.flatMap(b -> b.subdivideLots(16, 16, 1).stream())
			.collect(Collectors.toSet());
		TestCanvas.canvas.drawAll(encBlocks, DrawingEnclosedBlock.withColor(Color.green));

		return encBlocks.stream()
			.map(lot -> PolygonRasterizer.rasterize(lot.toPolygon()))
			.map(rasterized -> MaximalRectanlges.searchUntilSmallEnoughMutatingBitmap(rasterized, 21))
			.filter(list -> !list.isEmpty())
			.map(list -> new RectangleWithNeighbors(list.get(0), list.subList(1, list.size())))
			.collect(Collectors.toSet());
	}
}
