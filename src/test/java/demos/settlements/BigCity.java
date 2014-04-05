package demos.settlements;

import demos.Demos;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;

import java.awt.*;

public class BigCity {
    public static void main(String[] args) {
        TestCanvas canvas = Demos.createCanvas();
        GraphConstructor<Point2D, Line2D> gc = new GraphConstructor<>(Line2D::new)
                .vertex(0, new Point2D(50, 50))
                .vertex(1, new Point2D(150, 50))
                .vertex(2, new Point2D(50, 150))
                .vertex(3, new Point2D(150, 150))
                .vertex(4, new Point2D(200, 150))
                .vertex(5, new Point2D(200, 300))
                .vertex(6, new Point2D(350, 150))
                .vertex(7, new Point2D(350, 300))
                .vertex(8, new Point2D(32, 245))
                .vertex(9, new Point2D(108, 214))
                .vertex(10, new Point2D(152, 298))
                .vertex(11, new Point2D(67, 347))
                .edge(1, 4)
                .cycle(0, 1, 3, 2)
                .cycle(8, 9, 10, 11)
                .edge(10, 5)
                .edge(9, 2)
                .cycle(4, 5, 7, 6);
        SimpleGraph<Point2D,Line2D> graph = gc.graph();
        canvas.fillBackground(Color.BLACK);
        for (int i = 0; i < 1; i++) {
            City city = new CityBuilder(graph)
                    .withDefaults()
                    .withMaxStartPointsPerCycle(4)
                    .withRoadsFromPoint(3)
                    .withSecondaryRoadNetworkDeviationAngle(0.7)
                    .withConnectivity(0.5)
                    .withRoadSegmentLength(40, 50)
                    .withSnapSize(20)
                    .withCanvas(canvas)
                    .withSeed(i)
                    .build();
            canvas.draw(city, new CityDrawer());
        }
    }
}
