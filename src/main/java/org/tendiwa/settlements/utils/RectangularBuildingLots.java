package org.tendiwa.settlements.utils;

import com.google.common.collect.Iterators;
import org.tendiwa.collections.Collectors;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePolygon;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.extensions.daveedvMaxRec.MaximalRectanlges;
import org.tendiwa.geometry.extensions.polygonRasterization.PolygonRasterizer;
import org.tendiwa.geometry.smartMesh.EnclosedCyclesSet;
import org.tendiwa.geometry.smartMesh.SmartMesh2D;
import org.tendiwa.settlements.LinkedPolygon;

import java.awt.Color;
import java.util.Iterator;
import java.util.Set;

public final class RectangularBuildingLots {
	private RectangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> placeInside(
		SmartMesh2D segment2DSmartMesh
	) {
		EnclosedCyclesSet enclosedCycles = new EnclosedCyclesSet(segment2DSmartMesh);

		Set<Polygon> encBlocks = segment2DSmartMesh.networks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream().filter(b -> !enclosedCycles.contains(b)))
			.flatMap(b -> b.shrinkToRegions(3.3).stream())
			.flatMap(b -> b.subdivideLots(16, 16, 0.5).stream())
			.collect(Collectors.toImmutableSet());

//		drawBlocks(encBlocks);

//		drawEnclosedBlocks(segment2DSmartMesh, enclosedCycles);

		return encBlocks.stream()
			.map(lot -> PolygonRasterizer.rasterizeToMutable(lot))
			.map(rasterized -> MaximalRectanlges.searchUntilSmallEnoughMutatingBitmap(rasterized, 21))
			.filter(list -> !list.isEmpty())
			.map(list -> new BasicRectangleWithNeighbors(list.get(0), list.subList(1, list.size())))
			.collect(Collectors.toImmutableSet());
	}

	private static void drawEnclosedBlocks(SmartMesh2D segment2DSmartMesh, EnclosedCyclesSet enclosedCycles) {
		TestCanvas.canvas.drawAll(
			segment2DSmartMesh.networks()
				.stream()
				.flatMap(n -> n.enclosedBlocks().stream().filter(enclosedCycles::contains))
				.flatMap(b -> b.shrinkToRegions(3.3).stream()),
			block -> new DrawablePolygon(block, Color.black)
		);
	}

	private static void drawBlocks(Set<LinkedPolygon> encBlocks) {
		Iterator<Color> colors = Iterators.cycle(Color.magenta, Color.cyan, Color.orange);
		TestCanvas.canvas.drawAll(
			encBlocks,
			block -> new DrawablePolygon(block, colors.next())
		);
	}
}
