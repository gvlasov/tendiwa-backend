package org.tendiwa.demos.settlements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.collections.Collectors;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingChain;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.drawing.extensions.StreetsColoring;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.buildings.PolylineProximity;
import org.tendiwa.settlements.networks.SegmentNetworkBuilder;
import org.tendiwa.settlements.networks.Segment2DSmartMesh;
import org.tendiwa.settlements.utils.*;

import java.awt.Color;
import java.util.*;
import java.util.stream.IntStream;

public class BigCityDemo implements Runnable {

	DrawableInto canvas;

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
//		canvas = new MagnifierCanvas(5, 63, 86, 600, 600);
		canvas = new TestCanvas(1, 600, 600);
		canvas.fillBackground(Color.black);
		TestCanvas.canvas = canvas;
		IntStream.range(0, 1).forEach(seed -> {
			Segment2DSmartMesh segment2DSmartMesh = new SegmentNetworkBuilder(graph)
				.withDefaults()
				.withMaxStartPointsPerCycle(5)
				.withRoadsFromPoint(2)
				.withSecondaryRoadNetworkDeviationAngle(0.5)
				.withConnectivity(1.0)
				.withRoadSegmentLength(20)
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
				segment2DSmartMesh.getFullRoadGraph(),
				segment2DSmartMesh,
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
			Collection<RectangleWithNeighbors> lots = RectangularBuildingLots
				.placeInside(segment2DSmartMesh);
			PolylineProximity polylineProximity = new PolylineProximity(streets, lots, 8);
			Set<RectangleWithNeighbors> recGroups = lots
				.stream()
				.filter(BuildingPlacesFilters.closeToRoads(streets, lots, 8))
				.collect(Collectors.toImmutableSet());

			drawLots(recGroups);

//			Set<EnclosedBlock> blocks = roadsPlanarGraphModel.getBlocks()
//				.stream()
//				.flatMap(b -> b.shrinkToRegions(3, 0).stream())
//				.collect(Collectors.toSet());
//			for (EnclosedBlock block : blocks) {
//				canvas.draw(block, DrawingEnclosedBlock.withColor(Color.lightGray));
//			}
		});
	}

	private void drawLots(Set<RectangleWithNeighbors> recGroups) {
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
	}
}
