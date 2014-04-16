package tests.graph;

import demos.Demos;
import demos.settlements.SampleGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.tendiwa.drawing.TestCanvas;
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
    private final double snapSize;

    public CityTest(
            int pointsPerCycle,
            int roadsFromPoint,
            double dAngle,
            double connectivity,
            double[] segmentLength,
            double snapSize
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
        TestCanvas canvas = Demos.createCanvas();
        GraphConstructor<Point2D, Line2D> gc = SampleGraph.create();
        SimpleGraph<Point2D, Line2D> graph = gc.graph();
        for (int i = 0; i < 1; i++) {
            new CityBuilder(graph)
                    .withDefaults()
                    .withMaxStartPointsPerCycle(pointsPerCycle)
                    .withRoadsFromPoint(roadsFromPoint)
                    .withSecondaryRoadNetworkDeviationAngle(dAngle)
                    .withConnectivity(connectivity)
                    .withRoadSegmentLength(segmentLength[0], segmentLength[1])
                    .withSnapSize(snapSize)
                    .withCanvas(canvas)
                    .withSeed(i)
                    .build();
        }
    }
}
