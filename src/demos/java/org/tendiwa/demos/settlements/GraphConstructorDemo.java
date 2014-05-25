package org.tendiwa.demos.settlements;

import org.tendiwa.demos.Demos;
import org.junit.Test;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;

public class GraphConstructorDemo {

    @Test
    public void constructGraph() {
        TestCanvas canvas = Demos.createCanvas();
        GraphConstructor<Point2D, Segment2D> gc = new GraphConstructor<>(Segment2D::new)
                .vertex(0, new Point2D(100, 100))
                .vertex(1, new Point2D(100, 200))
                .vertex(2, new Point2D(200, 100))
                .vertex(3, new Point2D(200, 200)).withEdgesTo(0, 1, 2)

                .vertex(4, new Point2D(300, 300))
                .edge(4, 5)
                .vertex(5, new Point2D(400, 400))

                .vertex(6, new Point2D(500, 300))
                .vertex(7, new Point2D(600, 300))
                .vertex(8, new Point2D(600, 400))
                .vertex(9, new Point2D(500, 400))
                .cycle(6, 7, 8, 9)

                .path(2, 4, 6);
        canvas.draw(gc.graph(), DrawingGraph.withAliases(gc, a -> a.x, a -> a.y));
    }
}
