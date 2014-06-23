package tests;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

/**
 * Created by suseika on 3/10/14.
 */
public class SimpleGraphTest {
    @Test
    public void test() {
        SimpleGraph<Point2D, Segment2D> graph = new SimpleGraph<>(new EdgeFactory<Point2D, Segment2D>() {
            @Override
            public Segment2D createEdge(Point2D point2D, Point2D point2D2) {
                return new Segment2D(point2D, point2D2);
            }
        });
        Point2D a = new Point2D(1, 1);
        Point2D b = new Point2D(0, 0);
        graph.addVertex(a);
        graph.addVertex(b);
        graph.addEdge(a, b);
        graph.addEdge(b, a);
        System.out.println(graph.edgeSet().size());
    }
}
