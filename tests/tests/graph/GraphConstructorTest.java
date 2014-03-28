package tests.graph;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.DrawingGraph;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class GraphConstructorTest {
    @Inject
    TestCanvas canvas;

    @Test
    public void constructGraph() {
        GraphConstructor<Point2D, Line2D> gc = new GraphConstructor<>(Line2D::new)
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
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
