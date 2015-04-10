package org.tendiwa.demos.settlements;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.collections.Collectors;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.MagnifierCanvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.*;
import org.tendiwa.geometry.Chain2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.smartMesh.Segment2DSmartMesh;
import org.tendiwa.geometry.smartMesh.SegmentNetworkBuilder;
import org.tendiwa.settlements.utils.BuildingPlacesFilters;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.settlements.utils.RectangularBuildingLots;
import org.tendiwa.settlements.utils.RoadRejector;
import org.tendiwa.settlements.utils.streetsDetector.DetectedStreets;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class BigCityDemo implements Runnable {

	DrawableInto canvas;

	public static void main(String[] args) {
		Demos.run(BigCityDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		SimpleGraph<Point2D, Segment2D> graph = createGraph();
		createCanvas();

		drawGraph(graph);

		IntStream.range(0, 1).forEach(seed -> {
			Segment2DSmartMesh segment2DSmartMesh = createMesh(graph, seed);
			buildAndDrawLots(segment2DSmartMesh);
//			leavesAnimation(segment2DSmartMesh);
			drawBlocks(segment2DSmartMesh);
		});
	}

	private void drawGraph(SimpleGraph<Point2D, Segment2D> graph) {
		TestCanvas.canvas.draw(graph, DrawingGraph.withColorAndVertexSize(Color.red, 2));
	}

	private Segment2DSmartMesh createMesh(SimpleGraph<Point2D, Segment2D> graph, int seed) {
		return new SegmentNetworkBuilder(graph)
			.withDefaults()
			.withMaxStartPointsPerCycle(5)
			.withRoadsFromPoint(2)
			.withSecondaryRoadNetworkDeviationAngle(0.5)
			.withRoadSegmentLength(20)
			.withSnapSize(5)
			.withSeed(seed)
			.withAxisAlignedSegments(false)
			.build();
	}

	private void drawBlocks(Segment2DSmartMesh segment2DSmartMesh) {
		segment2DSmartMesh.networks()
			.stream()
			.flatMap(n -> n.enclosedBlocks().stream())
			.flatMap(b -> b.shrinkToRegions(3, 0).stream())
			.forEach(
				block ->
					canvas.draw(block, DrawingEnclosedBlock.withColor(Color.lightGray))
			);
	}

	private SimpleGraph<Point2D, Segment2D> createGraph() {
		return FourCyclePenisGraph.create().graph();
	}

	private void createCanvas() {
		canvas = new MagnifierCanvas(5, 162, 215, 600, 600);
//		canvas = new TestCanvas(1, 600, 600);
		canvas.fillBackground(Color.black);
		TestCanvas.canvas = canvas;
	}

	private void buildAndDrawLots(Segment2DSmartMesh segment2DSmartMesh) {
		UndirectedGraph<Point2D, Segment2D> allRoads = RoadRejector.rejectPartOfNetworksBorders(
			segment2DSmartMesh.getFullRoadGraph(),
			segment2DSmartMesh,
			0.0,
			new Random(1)
		);
//			UndirectedGraph<Point2D, Segment2D> allRoads = pathGeometry.getFullRoadGraph();
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
		for (Chain2D street : streets) {
			Color streetColor = streetsColoring.get(street);
			canvas.draw(street, DrawingChain.withColorThin(streetColor));
		}
		Collection<RectangleWithNeighbors> lots = RectangularBuildingLots
			.placeInside(segment2DSmartMesh);
		Set<RectangleWithNeighbors> recGroups = lots
			.stream()
			.filter(BuildingPlacesFilters.closeToRoads(streets, lots, 8))
			.collect(Collectors.toImmutableSet());

		drawLots(recGroups);
	}

	private void leavesAnimation(Segment2DSmartMesh segment2DSmartMesh) {
		TestCanvas canvasB = new TestCanvas(1, 800, 600);
		GifBuilder gif = new GifBuilder(canvasB, 1, Logger.getRootLogger());
		canvasB.fillBackground(Color.black);
		canvasB.draw(segment2DSmartMesh.getFullRoadGraph(), DrawingGraph.withColorAndVertexSize(Color.red, 3));
		ImmutableSet<Segment2D> whats = segment2DSmartMesh.innerTreeSegmentsEnds();
		gif.saveFrame();
		canvasB.drawAll(whats, DrawingSegment2D.withColorThin(Color.black));
		canvasB.drawAll(whats, DrawingSegment2D.withColorThin(Color.black));
		canvasB.drawAll(whats, DrawingSegment2D.withColorThin(Color.black));
		canvasB.drawAll(segment2DSmartMesh.getFullRoadGraph().vertexSet(), DrawingPoint2D.withColorAndSize(Color
			.red, 3));
		gif.saveFrame();
		gif.saveAnimation("/home/suseika/test.gif");
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
