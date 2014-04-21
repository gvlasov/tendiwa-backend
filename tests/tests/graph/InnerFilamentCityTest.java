package tests.graph;

import demos.Demos;
import demos.settlements.CityDrawer;
import org.junit.Test;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;

public class InnerFilamentCityTest {
    @Test
    public void innerFilament() {
        GraphConstructor<Point2D, Line2D> gc = new GraphConstructor<>(Line2D::new)
                .vertex(0, new Point2D(200, 100))
                .vertex(1, new Point2D(400, 100))
                .vertex(2, new Point2D(500, 200))
                .vertex(3, new Point2D(500, 400))
                .vertex(4, new Point2D(400, 500))
                .vertex(5, new Point2D(200, 500))
                .vertex(6, new Point2D(100, 400))
                .vertex(7, new Point2D(100, 200))
                .vertex(8, new Point2D(300, 200))
                .vertex(9, new Point2D(400, 450))
                .cycle(0, 1, 2, 3, 4, 5, 6, 7)
                .edge(0, 8)
                .edge(6, 9);
        TestCanvas canvas = Demos.createCanvas();
        City city = new CityBuilder(gc.graph())
                .withDefaults()
                .withRoadSegmentLength(50)
                .build();
        canvas.draw(city, new CityDrawer());
        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
