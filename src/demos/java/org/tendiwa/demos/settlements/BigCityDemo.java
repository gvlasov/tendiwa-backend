package org.tendiwa.demos.settlements;

import com.google.common.collect.Lists;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.collections.Collectors;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.DrawableRectangle;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.MagnifierCanvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.*;
import org.tendiwa.geometry.Chain2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.smartMesh.SegmentNetworkBuilder;
import org.tendiwa.geometry.smartMesh.SmartMesh2D;
import org.tendiwa.settlements.utils.*;
import org.tendiwa.settlements.utils.streetsDetector.DetectedStreets;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BigCityDemo implements Runnable {

	Canvas canvas;

	public static void main(String[] args) {
		Demos.run(BigCityDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		FourCyclePenisGraph graph = new FourCyclePenisGraph();
		createCanvas();

		TestCanvas.canvas.draw(
			new DrawableGraph2D.CircleVertices(
				graph,
				Color.red,
				2
			)
		);

		SmartMesh2D segment2DSmartMesh = new SegmentNetworkBuilder(graph)
			.withDefaults()
			.withMaxStartPointsPerCycle(5)
			.withRoadsFromPoint(2)
			.withSecondaryRoadNetworkDeviationAngle(0.5)
			.withRoadSegmentLength(20)
			.withSnapSize(5)
			.withSeed(0)
			.withAxisAlignedSegments(false)
			.build();
		buildAndDrawLots(segment2DSmartMesh);
//		leavesAnimation(segment2DSmartMesh);
		drawBlocks(segment2DSmartMesh);
	}

	private void drawBlocks(SmartMesh2D segment2DSmartMesh) {
		segment2DSmartMesh.networks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream())
			.flatMap(b -> b.shrinkToRegions(3, 0).stream())
			.forEach(
				block ->
					canvas.draw(
						block,
						DrawingEnclosedBlock.withColor(Color.lightGray)
					)
			);
	}

	private void createCanvas() {
		canvas = new MagnifierCanvas(5, 162, 215, 600, 600);
//		canvas = new TestCanvas(1, 600, 600);
		canvas.fillBackground(Color.black);
		TestCanvas.canvas = canvas;
	}

	private void buildAndDrawLots(SmartMesh2D segment2DSmartMesh) {
		UndirectedGraph<Point2D, Segment2D> allRoads = RoadRejector.rejectPartOfNetworksBorders(
			segment2DSmartMesh.graph(),
			segment2DSmartMesh,
			0.0,
			new Random(1)
		);
//			UndirectedGraph<Point2D, Segment2D> allRoads = pathGeometry.graph();
		Set<Chain2D> streets = DetectedStreets
			.toChain2DStream(allRoads)
			.collect(Collectors.toImmutableSet());
		Map<Chain2D, Color> streetsColoring = Chain2DColoring.compute(
			streets,
			Lists.newArrayList(
				Color.red, Color.yellow, Color.green, Color.cyan, Color.magenta,
				Color.orange, Color.black, Color.lightGray, Color.gray
			)
		);
		canvas.drawAll(
			streets,
			street ->
				new DrawableChain2D.Thin(
					street,
					streetsColoring.get(street)
				)
		);
		Collection<RectangleWithNeighbors> lots = RectangularBuildingLots
			.placeInside(segment2DSmartMesh);
		Set<RectangleWithNeighbors> recGroups = lots
			.stream()
			.filter(BuildingPlacesFilters.closeToRoads(streets, lots, 8))
			.collect(Collectors.toImmutableSet());

		drawLots(recGroups);
	}

	private void drawLots(Set<RectangleWithNeighbors> recGroups) {
		for (BasicRectangleWithNeighbors rectangleWithNeighbors : recGroups) {
			canvas.draw(
				new DrawableRectangle.Outlined(
					rectangleWithNeighbors.rectangle,
					Color.blue,
					Color.gray
				)
			);
			canvas.drawAll(
				rectangleWithNeighbors.neighbors,
				neighbor -> new DrawableRectangle.Outlined(
					neighbor,
					Color.magenta,
					Color.magenta.darker()
				)
			);
		}
	}
}
