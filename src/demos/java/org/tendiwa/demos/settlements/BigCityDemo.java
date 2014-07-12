package org.tendiwa.demos.settlements;

import com.google.inject.Inject;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.data.SampleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.*;

import java.awt.Color;
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
//        canvas.draw(graph, DrawingGraph.multicoloredEdges());
//        for (int i = 0; i < 100; i++) {
		TestCanvas.canvas = canvas;
		IntStream.range(0, 1).forEach(seed -> {
			System.out.println(seed);
			City city = new CityBuilder(graph)
				.withDefaults()
				.withMaxStartPointsPerCycle(1)
				.withRoadsFromPoint(4)
				.withSecondaryRoadNetworkDeviationAngle(0.3)
				.withConnectivity(0)
				.withRoadSegmentLength(40, 50)
				.withSnapSize(1)
				.withSeed(seed)
				.withAxisAlignedSegments(false)
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
				.flatMap(b -> b.shrinkToRegions(3.3, seed).stream())
				.flatMap(b -> b.subdivideLots(20, 20, 1).stream())
				.collect(Collectors.toSet());
			for (EnclosedBlock block : encBlocks) {
				canvas.draw(block, DrawingEnclosedBlock.withColor(Color.lightGray));
			}
		});

	}
}
