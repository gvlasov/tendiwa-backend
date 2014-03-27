package tests.graph;

import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;

public class TestGraphConstructor {
    @Test
    public void constructGraph() {
        new GraphConstructor<>()
                .vertex('a', new Point2D(100, 100))
                .vertex('b', new Point2D(120, 120))
                .vertex('c', new Point2D(140, 90))
                .edge('a', 'b')
                .edge('b', 'c')
                .edge('c', 'a')
                .graph();
    }
}
