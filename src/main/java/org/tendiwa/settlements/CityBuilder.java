package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;

import java.util.Random;

/**
 * A fluent builder to create instances of {@link City}.
 */
public class CityBuilder {
    /**
     * @see #withSamplesPerStep(int)
     */
    public static final int DEFAULT_SAMPLES_PER_STEP = 8;
    /**
     * @see #withRoadsFromPoint(int)
     */
    public static final int DEFAULT_ROADS_FROM_POINT = 4;
    /**
     * @see #withConnectivity(double)
     */
    public static final double DEFAULT_CONNECTIVITY = 1.0;
    /**
     * @see #withRoadSegmentLength(double)
     */
    public static final double DEFAULT_ROAD_SEGMENT_LENGTH = 10.;
    /**
     * @see #withSnapSize(double)
     */
    public static final double DEFAULT_SNAP_SIZE = 4.;
    /**
     * @see #withDeviationAngle(double)
     */
    public static final double DEFAULT_DEVIATION_ANGLE = Math.toRadians(20);
    /**
     * @see #withMaxStartPointsPerCycle(int)
     */
    public static final int DEFAULT_MAX_START_POINTS_PER_CYCLE = 2;
    /**
     * @see #withSecondaryRoadNetworkDeviationAngle(double)
     */
    public static final double DEFAULT_SECONDARY_ROAD_NETWORK_DEVIATION_ANGLE = 0.1;
    /**
     * @see #withSampleRadius(double)
     */
    private static final double DEFAULT_SAMPLE_RADIUS = 30.;
    /**
     * @see #withSecondaryRoadNetworkRoadLengthDeviation(double)
     */
    public static final double DEFAULT_SECONDARY_ROAD_NETWORK_ROAD_LENGTH_DEVIATION = 0;
    private UndirectedGraph<Point2D, Segment2D> graph;
    private Double sampleRadius;
    private Integer samplesPerStep;
    private Double deviationAngle;
    private Integer roadsFromPoint;
    private Double connectivity;
    private Double roadSegmentLength;
    private Double snapSize;
    private Integer maxNumOfStartPoints;
    private Double secondaryRoadNetworkDeviationAngle;
    private Double secondaryRoadNetworkRoadLengthDeviation;
    private TestCanvas canvas;
    private Random seededRandom;

    /**
     * Starts constructing a City defined by high level graph {@code graph}.
     *
     * @param graph
     *         The high level road graph of a city.
     * @see City#highLevelRoadGraph
     * @see [Kelly 4.2.0]
     */
    public CityBuilder(UndirectedGraph<Point2D, Segment2D> graph) {
        this.graph = graph;
    }

    public CityBuilder withCanvas(TestCanvas canvas) {
        this.canvas = canvas;
        return this;
    }


    @SuppressWarnings("unused")
    public CityBuilder withMaxStartPointsPerCycle(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("NumOfStartPoints must be at least 1");
        }
        this.maxNumOfStartPoints = amount;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withSampleRadius(double sampleRadius) {
        if (sampleRadius <= 0) {
            throw new IllegalArgumentException("Sample radius must be > 0");
        }
        this.sampleRadius = sampleRadius;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withRoadSegmentLength(double length) {
        if (length <= 0) {
            throw new IllegalArgumentException(
                    "roadSegmentLength must be > 0 (" + length + " provided)"
            );
        }
        this.roadSegmentLength = length;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withRoadSegmentLength(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
        this.roadSegmentLength = min / 2 + max / 2;
        this.secondaryRoadNetworkRoadLengthDeviation = (max - min) / 2;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withSamplesPerStep(int samplesPerStep) {
        if (samplesPerStep <= 0) {
            throw new IllegalArgumentException("Samples per step must be > 0");
        }
        this.samplesPerStep = samplesPerStep;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withDeviationAngle(double dAngle) {
        if (dAngle < 0 || dAngle >= Math.PI/4) {
            throw new IllegalArgumentException(
                    "Deviation angle must be >= 0 and < Math.PI/4 (" + dAngle + " provided)"
            );
        }
        this.deviationAngle = dAngle;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withSecondaryRoadNetworkDeviationAngle(double dAngle) {
        if (Math.abs(secondaryRoadNetworkDeviationAngle) >= Math.PI * 2) {
            throw new IllegalArgumentException(
                    "secondaryRoadNetworkDeviationAngle must be in [0; Math.PI*2)"
            );
        }
        this.secondaryRoadNetworkDeviationAngle = dAngle;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withSecondaryRoadNetworkRoadLengthDeviation(double dLength) {
        if (Math.abs(dLength) >= roadSegmentLength) {
            throw new IllegalArgumentException(
                    "secondaryRoadNetworkRoadLengthDeviation can't be greater than " +
                            "roadSegmentLength (the former is " + secondaryRoadNetworkDeviationAngle + ", " +
                            "the latter is " + roadSegmentLength + ")"
            );
        }
        this.secondaryRoadNetworkRoadLengthDeviation = dLength;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withConnectivity(double connectivity) {
        if (connectivity < 0 || connectivity > 1) {
            throw new IllegalArgumentException(
                    "Connectivity must be < 0 and > 1 (" + connectivity + " provided)"
            );
        }
        this.connectivity = connectivity;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withRoadsFromPoint(int amount) {
        if (amount < 2) {
            throw new IllegalArgumentException("roadsFromPoint must be >= 2");
        }
        this.roadsFromPoint = amount;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withSnapSize(double snapSize) {
        if (snapSize < 0) {
            throw new IllegalArgumentException("snapSize must be >= 0");
        }
        this.snapSize = snapSize;
        return this;
    }


    /**
     * Fills builder config with default parameters from this class's DEFAULT_* fields.
     *
     * @return this
     */
    @SuppressWarnings("unused")
    public CityBuilder withDefaults() {
        sampleRadius = DEFAULT_SAMPLE_RADIUS;
        samplesPerStep = DEFAULT_SAMPLES_PER_STEP;
        roadsFromPoint = DEFAULT_ROADS_FROM_POINT;
        connectivity = DEFAULT_CONNECTIVITY;
        roadSegmentLength = DEFAULT_ROAD_SEGMENT_LENGTH;
        snapSize = DEFAULT_SNAP_SIZE;
        deviationAngle = DEFAULT_DEVIATION_ANGLE;
        maxNumOfStartPoints = DEFAULT_MAX_START_POINTS_PER_CYCLE;
        secondaryRoadNetworkDeviationAngle = DEFAULT_SECONDARY_ROAD_NETWORK_DEVIATION_ANGLE;
        secondaryRoadNetworkRoadLengthDeviation = DEFAULT_SECONDARY_ROAD_NETWORK_ROAD_LENGTH_DEVIATION;
        seededRandom = new Random(0);
        return this;
    }

    @SuppressWarnings("unused")
    public City build() {
        if (graph == null) {
            throw new IllegalStateException("Graph not set");
        }
        if (sampleRadius == null) {
            throw new IllegalStateException("sampleRadius not set");
        }
        if (samplesPerStep == null) {
            throw new IllegalStateException("samplesPerStep not set");
        }
        if (deviationAngle == null) {
            throw new IllegalStateException("deviationAngle not set");
        }
        if (roadsFromPoint == null) {
            throw new IllegalStateException("roadsFromPoint not set");
        }
        if (connectivity == null) {
            throw new IllegalStateException("connectivity not set");
        }
        if (roadSegmentLength == null) {
            throw new IllegalStateException("roadSegmentLength not set");
        }
        if (snapSize == null) {
            throw new IllegalStateException("snapSize not set");
        }
        if (secondaryRoadNetworkDeviationAngle == null) {
            throw new IllegalStateException("secondaryRoadNetworkDeviationAngle not set");
        }
        if (maxNumOfStartPoints == null) {
            throw new IllegalStateException("maxNumOfStartPoints not set");
        }
        if (secondaryRoadNetworkRoadLengthDeviation == null) {
            throw new IllegalStateException("secondaryRoadNetworkRoadLengthDeviation not set");
        }
        if (seededRandom == null) {
            throw new IllegalStateException("seed not set");
        }
        final Random random = new Random(4);
       return new City(
                new RoadGraph(graph.vertexSet(), graph.edgeSet()),
                sampleFan -> {
                    int rand = random.nextInt(sampleFan.size());
                    return sampleFan.toArray(new Point2D[sampleFan.size()])[rand];
                },
                sampleRadius,
                samplesPerStep,
                deviationAngle,
                seededRandom,
                roadsFromPoint,
                connectivity,
                roadSegmentLength,
                snapSize,
                maxNumOfStartPoints,
                secondaryRoadNetworkDeviationAngle,
                secondaryRoadNetworkRoadLengthDeviation,
                canvas
        );
    }

    public CityBuilder withSeed(int i) {
        seededRandom = new Random(i);
        return this;
    }
}
