package org.tendiwa.demos.settlements;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.extensions.DrawingCell;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingMinimalCycle;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.StraightSkeleton;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.geometry.extensions.straightSkeleton.PolygonShrinker;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.TwakStraightSkeleton;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;
import org.tendiwa.settlements.NetworkWithinCycle;

import java.awt.*;

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

		for (MinimalCycle<Point2D, Segment2D> block : city.getBlocks()) {
			PolygonShrinker.canvas = canvas;
			StraightSkeleton skeleton = TwakStraightSkeleton.create(block.vertexList());
			UndirectedGraph<Point2D,Segment2D> shrunkBlock = skeleton.cap(3);
			canvas.draw(shrunkBlock, DrawingGraph.withColor(Color.blue));
		}
//        }
    }
}
