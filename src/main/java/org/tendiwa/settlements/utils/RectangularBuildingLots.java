package org.tendiwa.settlements.utils;

import com.google.common.collect.Iterators;
import org.tendiwa.collections.Collectors;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.geometry.extensions.daveedvMaxRec.MaximalRectanlges;
import org.tendiwa.geometry.extensions.polygonRasterization.PolygonRasterizer;
import org.tendiwa.settlements.EnclosedBlock;
import org.tendiwa.settlements.networks.EnclosedCyclesSet;
import org.tendiwa.settlements.networks.RoadsPlanarGraphModel;

import java.awt.Color;
import java.util.Iterator;
import java.util.Set;

public final class RectangularBuildingLots {
	private RectangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> placeInside(
		RoadsPlanarGraphModel roadsPlanarGraphModel
	) {
		EnclosedCyclesSet enclosedCycles = new EnclosedCyclesSet(roadsPlanarGraphModel);

		Set<EnclosedBlock> encBlocks = roadsPlanarGraphModel.getNetworks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream().filter(b -> !enclosedCycles.contains(b)))
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.flatMap(b -> b.subdivideLots(16, 16, 0.5).stream())
				// TODO: toImmutableSet
			.collect(Collectors.toImmutableSet());

		Iterator<Color> colors = Iterators.cycle(Color.magenta, Color.cyan, Color.orange);
		encBlocks
			.forEach(block -> TestCanvas.canvas.draw(block, DrawingEnclosedBlock.withColor(colors.next())));

		roadsPlanarGraphModel.getNetworks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream().filter(enclosedCycles::contains))
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.forEach(b -> TestCanvas.canvas.draw(b, DrawingEnclosedBlock.withColor(Color.black)));

		return encBlocks.stream()
			.map(lot -> PolygonRasterizer.rasterizeToMutable(lot.toPolygon()))
			.map(rasterized -> MaximalRectanlges.searchUntilSmallEnoughMutatingBitmap(rasterized, 21))
			.filter(list -> !list.isEmpty())
			.map(list -> new RectangleWithNeighbors(list.get(0), list.subList(1, list.size())))
			.collect(Collectors.toImmutableSet());
	}
}
