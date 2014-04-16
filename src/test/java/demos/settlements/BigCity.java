package demos.settlements;

import com.google.inject.internal.util.$ToStringBuilder;
import demos.Demos;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;

import java.awt.*;

public class BigCity {

    @Test
    public void buildCity() {
        main(null);
    }

    public static void main(String[] args) {
//        new BigCity().buildCity();
        GraphConstructor<Point2D, Line2D> gc = SampleGraph.create();
        SimpleGraph<Point2D, Line2D> graph = gc.graph();
        for (int i = 0; i < 1; i++) {
            new CityBuilder(graph)
                    .withDefaults()
                    .withMaxStartPointsPerCycle(3)
                    .withRoadsFromPoint(4)
                    .withSecondaryRoadNetworkDeviationAngle(0.2)
                    .withConnectivity(0)
                    .withRoadSegmentLength(10, 10)
                    .withSnapSize(1)
                    .withSeed(i)
                    .withCanvas(Demos.createCanvas())
                    .build();
        }
        System.out.println("done");
//        TestCanvas canvas = Demos.createCanvas();
//        canvas.fillBackground(Color.BLACK);
//        GraphConstructor<Point2D, Line2D> gc = SampleGraph.create();
//        SimpleGraph<Point2D, Line2D> graph = gc.graph();
//        for (int i = 0; i < 100; i++) {
//            City city = new CityBuilder(graph)
//                    .withDefaults()
//                    .withMaxStartPointsPerCycle(5)
//                    .withRoadsFromPoint(4)
//                    .withSecondaryRoadNetworkDeviationAngle(0.0)
//                    .withConnectivity(0)
//                    .withRoadSegmentLength(6, 11)
//                    .withSnapSize(0)
//                    .withCanvas(canvas)
//                    .withSeed(i)
//                    .build();
//            canvas.draw(city, new CityDrawer());
//        }
    }


}
