package org.tendiwa.settlements.utils;

import org.tendiwa.geometry.extensions.PolygonRasterizer;
import org.tendiwa.geometry.extensions.daveedvMaxRec.MaximalRectanlges;
import org.tendiwa.settlements.EnclosedBlock;
import org.tendiwa.settlements.InnerEnclosedBlocksIndex;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.RoadsPlanarGraphModel;

import java.util.Set;
import java.util.stream.Collectors;

public final class RectangularBuildingLots {
	private RectangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> placeInside(
		RoadsPlanarGraphModel roadsPlanarGraphModel,
		InnerEnclosedBlocksIndex index
	) {
		Set<EnclosedBlock> encBlocks = roadsPlanarGraphModel
			.getBlocks()
			.stream()
			.filter(b -> !index.contains(b))
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.flatMap(b -> b.subdivideLots(16, 16, 1).stream())
			.collect(Collectors.toSet());
//		Iterator<Color> colors = Iterators.cycle(Color.magenta, Color.cyan, Color.orange);
//		for (EnclosedBlock block : roadsPlanarGraphModel.getBlocks()) {
//			TestCanvas.canvas.draw(block, DrawingEnclosedBlock.withColor(colors.next()));
//		}


		return encBlocks.stream()
			.map(lot -> PolygonRasterizer.rasterize(lot.toPolygon()))
			.map(rasterized -> MaximalRectanlges.searchUntilSmallEnoughMutatingBitmap(rasterized, 21))
			.filter(list -> !list.isEmpty())
			.map(list -> new RectangleWithNeighbors(list.get(0), list.subList(1, list.size())))
			.collect(Collectors.toSet());
	}
}
