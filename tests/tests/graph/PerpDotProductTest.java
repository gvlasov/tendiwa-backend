package tests.graph;

import org.junit.Test;

import static org.tendiwa.graphs.MinimumCycleBasis.perpDotProduct;

public class PerpDotProductTest {
    @Test
    public void test() {
        double v = perpDotProduct(
                new double[]{2, 2},
                new double[]{3, 2}
        );
        System.out.println(v);
    }
}
