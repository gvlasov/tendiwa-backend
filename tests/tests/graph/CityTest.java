package tests.graph;

import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.CityBuilder;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CityTest {

    private final int pointsPerCycle;
    private final int roadsFromPoint;
    private final double dAngle;
    private final double connectivity;
    private final double[] segmentLength;
    private final int snapSize;

    public CityTest(
            int pointsPerCycle,
            int roadsFromPoint,
            double dAngle,
            double connectivity,
            double[] segmentLength,
            int snapSize
    ) {
        this.pointsPerCycle = pointsPerCycle;
        this.roadsFromPoint = roadsFromPoint;
        this.dAngle = dAngle;
        this.connectivity = connectivity;
        this.segmentLength = segmentLength;
        this.snapSize = snapSize;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {2, 4, 0.1, 1, new double[]{10, 14}, 4},
                {3, 4, 0.1, 1, new double[]{10, 14}, 4},
                {4, 4, 0.1, 1, new double[]{10, 14}, 4},
                {5, 4, 0.1, 1, new double[]{10, 14}, 4},
                {2, 3, 0.1, 1, new double[]{10, 14}, 4},
                {2, 3, 0.2, 1, new double[]{10, 14}, 4},
                {2, 3, 0.2, 1, new double[]{10, 14}, 7},
                {2, 3, 0.2, 1, new double[]{10, 14}, 8},
                {2, 3, 0.2, 1, new double[]{20, 30}, 10},
                {2, 3, 0.9, 1, new double[]{20, 30}, 10},
                {2, 4, 0.9, 1, new double[]{20, 30}, 10},
                {2, 5, 0.9, 1, new double[]{20, 30}, 10},
                {2, 5, 0.9, 0.5, new double[]{20, 30}, 10},
                {2, 5, 0.9, 0.0, new double[]{20, 30}, 10},
                {3, 4, 0.0, 0.0, new double[]{10, 10}, 0},
        });
    }

    @Test
    public void buildCity() {
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
        SimpleGraph<Point2D, Line2D> graph = gc.graph();
        for (int i = 0; i < 5; i++) {
            new CityBuilder(graph)
                    .withDefaults()
                    .withMaxStartPointsPerCycle(pointsPerCycle)
                    .withRoadsFromPoint(roadsFromPoint)
                    .withSecondaryRoadNetworkDeviationAngle(dAngle)
                    .withConnectivity(connectivity)
                    .withRoadSegmentLength(segmentLength[0], segmentLength[1])
                    .withSnapSize(snapSize)
                    .withSeed(i)
                    .build();
        }
    }
}
