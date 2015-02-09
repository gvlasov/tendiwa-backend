package org.tendiwa.settlements.utils;

import com.google.common.collect.Iterators;
import org.tendiwa.collections.Collectors;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.geometry.extensions.daveedvMaxRec.MaximalRectanlges;
import org.tendiwa.geometry.extensions.polygonRasterization.PolygonRasterizer;
import org.tendiwa.settlements.EnclosedBlock;
import org.tendiwa.settlements.networks.EnclosedCyclesSet;
import org.tendiwa.settlements.networks.SegmentNetwork;

import java.awt.Color;
import java.util.Iterator;
import java.util.Set;

public final class RectangularBuildingLots {
	private RectangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> placeInside(
		SegmentNetwork segmentNetwork
	) {
		EnclosedCyclesSet enclosedCycles = new EnclosedCyclesSet(segmentNetwork);

		Set<EnclosedBlock> encBlocks = segmentNetwork.getNetworks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream().filter(b -> !enclosedCycles.contains(b)))
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.flatMap(b -> b.subdivideLots(16, 16, 0.5).stream())
				// TODO: toImmutableSet
			.collect(Collectors.toImmutableSet());

		drawBlocks(encBlocks);

		drawEnclosedBlocks(segmentNetwork, enclosedCycles);

		return encBlocks.stream()
			.map(lot -> PolygonRasterizer.rasterizeToMutable(lot.toPolygon()))
			.map(rasterized -> MaximalRectanlges.searchUntilSmallEnoughMutatingBitmap(rasterized, 21))
			.filter(list -> !list.isEmpty())
			.map(list -> new RectangleWithNeighbors(list.get(0), list.subList(1, list.size())))
			.collect(Collectors.toImmutableSet());
	}

	private static void drawEnclosedBlocks(SegmentNetwork segmentNetwork, EnclosedCyclesSet enclosedCycles) {
		segmentNetwork.getNetworks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream().filter(enclosedCycles::contains))
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.forEach(b -> TestCanvas.canvas.draw(b, DrawingEnclosedBlock.withColor(Color.black)));
	}

	private static void drawBlocks(Set<EnclosedBlock> encBlocks) {
		Iterator<Color> colors = Iterators.cycle(Color.magenta, Color.cyan, Color.orange);
		encBlocks
			.forEach(block -> TestCanvas.canvas.draw(block, DrawingEnclosedBlock.withColor(colors.next())));
	}
}
