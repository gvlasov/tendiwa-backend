package org.tendiwa.demos.settlements;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawingGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;

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
        canvas.draw(graph, DrawingGraph.multicoloredEdges());
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            City city = new CityBuilder(graph)
                    .withDefaults()
                    .withMaxStartPointsPerCycle(5)
                    .withRoadsFromPoint(4)
                    .withSecondaryRoadNetworkDeviationAngle(0.0)
                    .withConnectivity(0)
                    .withRoadSegmentLength(6, 11)
                    .withSnapSize(0)
                    .withCanvas(canvas)
                    .withSeed(i)
                    .build();
            canvas.draw(city, new CityDrawer());
        }
    }
}
