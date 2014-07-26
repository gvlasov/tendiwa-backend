package org.tendiwa.demos.settlements;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.data.SampleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.*;

import java.awt.Color;
import java.util.Iterator;
import java.util.Set;
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
//        canvas.draw(graph, DrawingGraph.multicoloredEdges());
//        for (int i = 0; i < 100; i++) {
		TestCanvas.canvas = canvas;
		IntStream.range(0, 1).forEach(seed -> {
			CityGeometry cityGeometry = new CityBuilder(graph)
				.withDefaults()
				.withMaxStartPointsPerCycle(5)
				.withRoadsFromPoint(4)
				.withSecondaryRoadNetworkDeviationAngle(1)
				.withConnectivity(0.3)
				.withRoadSegmentLength(30, 35)
				.withSnapSize(4)
				.withSeed(seed)
				.withAxisAlignedSegments(false)
				.build();

			Set<RectangleWithNeighbors> recGroups = RecgangularBuildingLots.findIn(cityGeometry);
			canvas.draw(cityGeometry, new CityDrawer());
			Iterator<Color> colors = Iterators.cycle(Color.green, Color.blue, Color.cyan, Color.orange, Color.magenta);
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
//			for (EnclosedBlock block : encBlocks) {
//				canvas.draw(block, DrawingEnclosedBlock.withColor(Color.lightGray));
//			}
		});
	}
}
