package org.tendiwa.demos.settlements;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.data.SampleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingChain;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.TwakStraightSkeleton;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.*;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
		GraphConstructor<Point2D, Segment2D> gc = SampleGraph.create();
		SimpleGraph<Point2D, Segment2D> graph = gc.graph();
		TestCanvas.canvas = canvas;
		IntStream.range(0, 1).forEach(seed -> {
			CityGeometry cityGeometry = new CityGeometryBuilder(graph)
				.withDefaults()
				.withMaxStartPointsPerCycle(5)
				.withRoadsFromPoint(4)
				.withSecondaryRoadNetworkDeviationAngle(1)
				.withConnectivity(0.3)
				.withRoadSegmentLength(60, 80)
				.withSnapSize(9)
				.withSeed(seed)
				.withAxisAlignedSegments(false)
				.build();

			Set<RectangleWithNeighbors> recGroups = RectangularBuildingLots.findIn(cityGeometry);
//			canvas.draw(cityGeometry, new CityDrawer());
			Iterator<Color> colors = Iterators.cycle(
				Color.getHSBColor((float) 0.5, 1, (float) 0.8),
				Color.getHSBColor((float) 0.25, 1, (float) 0.8),
				Color.getHSBColor(0, 1, 1),
				Color.getHSBColor((float) 0.5, 1, 1),
				Color.getHSBColor((float) 0.25, 1, 1),
				Color.getHSBColor((float) 0.80, 1, 1),
				Color.getHSBColor(0, (float) 0.5, 1),
				Color.getHSBColor((float) 0.37, 1, 1),
				Color.getHSBColor((float) 0.37, 1, (float) 0.0),
				Color.getHSBColor((float) 0.62, 1, (float) 0.8)
			);
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
			Set<SecondaryRoadNetworkBlock> blocks = cityGeometry.getBlocks()
//				.stream()
//				.flatMap(b -> b.shrinkToRegions(3, 0).stream())
//				.collect(Collectors.toSet());
			;

			for (EnclosedBlock block : blocks) {
				canvas.draw(block, DrawingEnclosedBlock.withColor(Color.lightGray));
			}
			UndirectedGraph<Point2D, Segment2D> allRoads = cityGeometry.getFullRoadGraph();
			Set<List<Point2D>> streets = StreetsDetector.detectStreets(allRoads);
			for (List<Point2D> street : streets) {
//				canvas.draw(street, DrawingChain.withColor(colors.next()));
			}
		});
	}
}
