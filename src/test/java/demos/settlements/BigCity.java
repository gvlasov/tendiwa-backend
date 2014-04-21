package demos.settlements;

import demos.Demos;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;
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
        TestCanvas canvas = Demos.createCanvas();
        canvas.fillBackground(Color.BLACK);
        GraphConstructor<Point2D, Line2D> gc = SampleGraph.create();
        SimpleGraph<Point2D, Line2D> graph = gc.graph();
        for (int i = 0; i < 100; i++) {
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
