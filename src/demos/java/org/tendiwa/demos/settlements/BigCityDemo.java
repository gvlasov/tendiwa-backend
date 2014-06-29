package org.tendiwa.demos.settlements;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.extensions.*;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.*;

import java.awt.*;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class BigCityDemo implements Runnable {

	public static void main(String[] args) {
		Demos.run(BigCityDemo.class);
	}

	@Override
	public void run() {
		TestCanvas canvas = Demos.createCanvas();
		GraphConstructor<Point2D, Segment2D> gc = SampleGraph.create();
		SimpleGraph<Point2D, Segment2D> graph = gc.graph();
//        canvas.draw(graph, DrawingGraph.multicoloredEdges());
//        for (int i = 0; i < 100; i++) {
		City city = new CityBuilder(graph)
			.withDefaults()
			.withMaxStartPointsPerCycle(5)
			.withRoadsFromPoint(4)
			.withSecondaryRoadNetworkDeviationAngle(0.0)
			.withConnectivity(0.1)
			.withRoadSegmentLength(10, 20)
			.withSnapSize(4)
			.withCanvas(canvas)
			.withSeed(1)
			.build();
//		for (NetworkWithinCycle network : city.getCells()) {
//			for (Point2D filamentEnd : network.filamentEnds()) {
//				canvas.draw(filamentEnd, DrawingPoint2D.withColorAndSize(Color.green, 5));
//			}
//		}
		canvas.draw(city, new CityDrawer());

//        city
//                .getCells()
//                .stream()
//                .flatMap(network -> network.exitsOnCycles().stream())
//                .forEach(point -> canvas.draw(point.toCell(), DrawingCell.withColorAndSize(Color.GREEN, 4)));
		Set<BlockRegion> blocks = city
			.getBlocks()
			.stream()
			.flatMap(b -> b.shrinkToRegions(2, new Random(0), canvas).stream())
			.flatMap(b -> b.subdivideLots(7, 7, 1).stream())
			.collect(Collectors.toSet());


		for (BlockRegion block : blocks) {
			canvas.draw(block, DrawingEnclosedBlock.withColor(Color.blue));
		}
		System.out.println("done");

//        }
	}
}
