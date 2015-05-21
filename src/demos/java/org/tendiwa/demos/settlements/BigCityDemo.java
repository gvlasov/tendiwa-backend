package org.tendiwa.demos.settlements;

import com.google.common.collect.Lists;
import org.tendiwa.collections.Collectors;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.DrawableRectangle;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.MagnifierCanvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.*;
import org.tendiwa.geometry.Chain2D;
import org.tendiwa.geometry.extensions.straightSkeleton.ShrinkedPolygon;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.geometry.smartMesh.MeshedNetworkBuilder;
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

		MeshedNetwork segment2DSmartMesh = new MeshedNetworkBuilder(graph)
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

	private void drawBlocks(MeshedNetwork meshedNetwork) {
		canvas.drawAll(
			meshedNetwork.meshCells()
				.stream()
				.flatMap(b -> new ShrinkedPolygon(b, 3).stream()),
			polygon -> new DrawablePolygon.Thin(polygon, Color.lightGray)
		);
	}

	private void createCanvas() {
		canvas = new MagnifierCanvas(5, 162, 215, 600, 600);
//		canvas = new TestCanvas(1, 600, 600);
		canvas.fillBackground(Color.black);
		TestCanvas.canvas = canvas;
	}

	private void buildAndDrawLots(MeshedNetwork network) {
		Graph2D allRoads = new NetworkGraphWithHolesInHull(
			network,
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
			.placeInside(network);
		Set<RectangleWithNeighbors> recGroups = lots
			.stream()
			.filter(BuildingPlacesFilters.closeToRoads(streets, lots, 8))
			.collect(Collectors.toImmutableSet());

		drawLots(recGroups);
	}

	private void drawLots(Set<RectangleWithNeighbors> recGroups) {
		for (RectangleWithNeighbors rectangleWithNeighbors : recGroups) {
			canvas.draw(
				new DrawableRectangle.Outlined(
					rectangleWithNeighbors.mainRectangle(),
					Color.blue,
					Color.gray
				)
			);
			canvas.drawAll(
				rectangleWithNeighbors.neighbors(),
				neighbor -> new DrawableRectangle.Outlined(
					neighbor,
					Color.magenta,
					Color.magenta.darker()
				)
			);
		}
	}
}
