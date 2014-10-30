package org.tendiwa.demos.settlements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingChain;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.drawing.extensions.StreetsColoring;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.networks.CityGeometryBuilder;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.networks.RoadsPlanarGraphModel;
import org.tendiwa.settlements.utils.BuildingPlacesFilters;
import org.tendiwa.settlements.utils.RectangularBuildingLots;
import org.tendiwa.settlements.utils.RoadRejector;
import org.tendiwa.settlements.utils.StreetsDetector;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BigCityDemo implements Runnable {

	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(BigCityDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		GraphConstructor<Point2D, Segment2D> gc = FourCyclePenisGraph.create();
		SimpleGraph<Point2D, Segment2D> graph = gc.graph();
//		SimpleGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
//			.vertex(0, new Point2D(50, 50))
//			.vertex(1, new Point2D(350, 50))
//			.vertex(2, new Point2D(350, 350))
//			.vertex(3, new Point2D(50, 350))
//			.cycle(0, 1, 2, 3)
//			.graph();
		TestCanvas.canvas = canvas;
		IntStream.range(0, 1).forEach(seed -> {
			RoadsPlanarGraphModel roadsPlanarGraphModel = new CityGeometryBuilder(graph)
				.withDefaults()
				.withMaxStartPointsPerCycle(5)
				.withRoadsFromPoint(2)
				.withSecondaryRoadNetworkDeviationAngle(0.5)
				.withConnectivity(0.0)
				.withRoadSegmentLength(10)
				.withSnapSize(5)
				.withSeed(seed)
				.withAxisAlignedSegments(false)
				.build();

//			canvas.draw(cityGeometry, new CityDrawer());
			Iterator<Color> colors = Iterators.cycle(
				Color.getHSBColor(0, (float) 0.5, 1),
				Color.getHSBColor((float) 0.37, 1, (float) 0.0),
				Color.getHSBColor((float) 0.5, 1, (float) 0.8),
				Color.getHSBColor((float) 0.25, 1, (float) 0.8),
				Color.getHSBColor(0, 1, 1),
				Color.getHSBColor((float) 0.5, 1, 1),
				Color.getHSBColor((float) 0.25, 1, 1),
				Color.getHSBColor((float) 0.80, 1, 1),
				Color.getHSBColor((float) 0.37, 1, 1),
				Color.getHSBColor((float) 0.62, 1, (float) 0.8)
			);
			UndirectedGraph<Point2D, Segment2D> allRoads = RoadRejector.rejectPartOfNetworksBorders(
				roadsPlanarGraphModel.getFullRoadGraph(),
				roadsPlanarGraphModel,
				1.0,
				new Random(1)
			);
//			UndirectedGraph<Point2D, Segment2D> allRoads = pathGeometry.getFullRoadGraph();
			Set<ImmutableList<Point2D>> streets = StreetsDetector.detectStreets(allRoads);
			Map<ImmutableList<Point2D>, Color> streetsColoring = StreetsColoring.compute(
				streets, Color.red, Color.blue,
				Color.green, Color.cyan, Color.magenta, Color.orange, Color.black, Color.lightGray, Color.gray
			);
			for (List<Point2D> street : streets) {
				Color streetColor = streetsColoring.get(street);
				canvas.draw(street, DrawingChain.withColor(streetColor));
			}
			Set<RectangleWithNeighbors> recGroups = RectangularBuildingLots
				.placeInside(roadsPlanarGraphModel)
				.stream()
				.filter(BuildingPlacesFilters.closeToRoads(streets, 8))
				.collect(Collectors.toSet());

			for (RectangleWithNeighbors rectangleWithNeighbors : recGroups) {
				canvas.draw(
					rectangleWithNeighbors.rectangle,
					DrawingRectangle.withColorAndBorder(Color.blue, Color.gray)
				);
				for (Rectangle neighbor : rectangleWithNeighbors.neighbors) {
					canvas.draw(
						neighbor,
						DrawingRectangle.withColorAndBorder(Color.magenta, Color.magenta.darker())
					);
				}
			}
//			Set<EnclosedBlock> blocks = roadsPlanarGraphModel.getBlocks()
//				.stream()
//				.flatMap(b -> b.shrinkToRegions(3, 0).stream())
//				.collect(Collectors.toSet());
//			for (EnclosedBlock block : blocks) {
//				canvas.draw(block, DrawingEnclosedBlock.withColor(Color.lightGray));
//			}
		});
	}
}
