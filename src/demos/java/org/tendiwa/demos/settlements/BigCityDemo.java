package org.tendiwa.demos.settlements;

import com.google.inject.Inject;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.GraphExplorer;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.*;

import java.awt.Color;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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
		NetworkToBlocks.canvas = canvas;
		TestCanvas.canvas = canvas;
		City city = new CityBuilder(graph)
			.withDefaults()
			.withMaxStartPointsPerCycle(5)
			.withRoadsFromPoint(4)
			.withSecondaryRoadNetworkDeviationAngle(0.3)
			.withConnectivity(0.1)
			.withRoadSegmentLength(30, 45)
			.withSnapSize(4)
//			.withCanvas(canvas)
			.withSeed(1)
			.withAxisAlignedSegments(true)
			.build();
//		for (NetworkWithinCycle network : city.getCells()) {
//			for (Point2D filamentEnd : network.filamentEnds()) {
//				canvas.draw(filamentEnd, DrawingPoint2D.withColorAndSize(Color.green, 5));
//			}
//		}
//		canvas.draw(city, new CityDrawer());

//        city
//                .getCells()
//                .stream()
//                .flatMap(network -> network.exitsOnCycles().stream())
//                .forEach(point -> canvas.draw(point.toCell(), DrawingCell.withColorAndSize(Color.GREEN, 4)));
//		PolygonShrinker.canvas = canvas;
		Set<SecondaryRoadNetworkBlock> blocks = city
			.getBlocks();
//			.stream()
//			.flatMap(b -> b.shrinkToRegions(6, new Random(0), canvas).stream())
//			.flatMap(b -> b.subdivideLots(8, 8, 0).stream())
//			.collect(Collectors.toSet());


		Set<EnclosedBlock> encBlocks = city
			.getBlocks()
			.stream()
			.flatMap(b -> b.shrinkToRegions(3, new Random(0), canvas).stream())
			.flatMap(b -> b.subdivideLots(8, 8, 0).stream())
			.collect(Collectors.toSet());
		for (EnclosedBlock block : encBlocks) {
			canvas.draw(block, DrawingEnclosedBlock.withColor(Color.lightGray));
		}

	}
}
