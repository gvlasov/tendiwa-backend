package org.tendiwa.demos.settlements;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;

public class NetworkWithHoles implements Runnable {
    public static void main(String[] args) {
        Demos.run(NetworkWithHoles.class);
    }

    @Override
    public void run() {
        TestCanvas canvas = new TestCanvas(2, 400, 400);
        SimpleGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
                .vertex(0, new Point2D(10, 10))
                .vertex(1, new Point2D(10, 110))
                .vertex(2, new Point2D(110, 110))
                .vertex(3, new Point2D(110, 10))
                .cycle(0, 1, 2, 3)
                .vertex(4, new Point2D(30, 30))
                .vertex(5, new Point2D(30,70))
                .vertex(6, new Point2D(70,70))
                .vertex(7, new Point2D(70,30))
                .cycle(4,5,6,7)
                .graph();
        City city = new CityBuilder(graph)
                .withDefaults()
                .build();
        canvas.draw(city, new CityDrawer());

    }
}
