package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.geometry.extensions.daveedvMaxRec.MaximalRectanlges;
import org.tendiwa.geometry.extensions.polygonRasterization.PolygonRasterizer;
import org.tendiwa.settlements.BlockRegion;
import org.tendiwa.settlements.EnclosedBlock;
import org.tendiwa.settlements.networks.EnclosedCyclesSet;
import org.tendiwa.settlements.networks.NetworkWithinCycle;
import org.tendiwa.settlements.networks.RoadsPlanarGraphModel;
import org.tendiwa.settlements.networks.SecondaryRoadNetworkBlock;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public final class RectangularBuildingLots {
	private RectangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> placeInside(
		RoadsPlanarGraphModel roadsPlanarGraphModel
	) {
		EnclosedCyclesSet enclosedCycles = new EnclosedCyclesSet(roadsPlanarGraphModel);
//		List<BlockRegion> encBlocks = collectBlocks(roadsPlanarGraphModel, enclosedCycles);

		List<EnclosedBlock> encBlocks = roadsPlanarGraphModel.getNetworks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream().filter(b -> !enclosedCycles.contains(b)))
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.flatMap(b -> b.subdivideLots(16, 16, 0.5).stream())
				// TODO: toImmutableSet
			.collect(Collectors.toList());

		Iterator<Color> colors = Iterators.cycle(Color.magenta, Color.cyan, Color.orange);
		encBlocks
			.forEach(block -> TestCanvas.canvas.draw(block, DrawingEnclosedBlock.withColor(colors.next())));

		roadsPlanarGraphModel.getNetworks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream().filter(enclosedCycles::contains))
			.flatMap(b -> b.shrinkToRegions(3.3, 0).stream())
			.forEach(b -> TestCanvas.canvas.draw(b, DrawingEnclosedBlock.withColor(Color.blue)));

		return encBlocks.stream()
			.map(lot -> PolygonRasterizer.rasterizeToMutable(lot.toPolygon()))
			.map(rasterized -> MaximalRectanlges.searchUntilSmallEnoughMutatingBitmap(rasterized, 21))
			.filter(list -> !list.isEmpty())
			.map(list -> new RectangleWithNeighbors(list.get(0), list.subList(1, list.size())))
			.collect(org.tendiwa.collections.Collectors.toImmutableSet());
	}

	private static List<BlockRegion> collectBlocks(
		RoadsPlanarGraphModel roadsPlanarGraphModel,
		EnclosedCyclesSet enclosedCycles
	) {
//		ImmutableSet<NetworkWithinCycle> networks = roadsPlanarGraphModel.getNetworks();
//		Collection<SecondaryRoadNetworkBlock> blocks = new ArrayList<>();
//		for (NetworkWithinCycle network : networks) {
//			network.enclosedBlocks().stream().filter(b -> !enclosedCycles.contains(b)).forEach(blocks::add);
//		}
//		Collection<BlockRegion> blockRegions = new LinkedHashSet<>();
//		for (SecondaryRoadNetworkBlock block : blocks) {
//			blockRegions.addAll(block.shrinkToRegions(3.3, 0));
//		}
//		List<BlockRegion> answer = new ArrayList<>();
//		for (BlockRegion blockRegion : blockRegions) {
//			answer.addAll(blockRegion.subdivideLots(16, 16, 0.5));
//		}
//		return answer;
		return null;

	}
}
