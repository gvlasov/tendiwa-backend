package tests.graph;

import com.google.inject.Inject;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingGraph;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class TestGraphConstructor {
    @Inject
    TestCanvas canvas;

    @Test
    public void constructGraph() {
        GraphConstructor<Point2D, DefaultEdge> constructor =
                GraphConstructor.<Point2D>create()
                        .vertex(1, new Point2D(100, 100))
                        .vertex(2, new Point2D(120, 120))
                        .vertex(3, new Point2D(140, 90))
                        .vertex(4, new Point2D(120, 150))
                        .vertex(5, new Point2D(140, 170))
                        .cycle(1, 2, 3, 4)
                        .edge(1, 3)
                        .path(2, 4, 5);
        canvas.draw(constructor.graph(), DrawingGraph.withAliases(constructor, a -> a.x, a -> a.y));
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
