package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

import java.util.Random;

public class CityBuilder {
    public static final int DEFAULT_SAMPLES_PER_STEP = 8;
    public static final int DEFAULT_PARAM_DEGREE = 4;
    public static final double DEFAULT_CONNECTIVITY = 1.0;
    public static final double DEFAULT_ROAD_SEGMENT_LENGTH = 10.;
    public static final double DEFAULT_SNAP_SIZE = 4.;
    public static final double DEFAULT_DEVIATION_ANGLE = Math.toRadians(20);
    public static final int DEFAULT_NUM_OF_START_POINTS = 2;
    public static final double DEFAULT_SECONDARY_ROAD_NETWORK_DEVIATION_ANGLE = 0.1;
    private static final double DEFAULT_SAMPLE_RADIUS = 30.;
    public static final double DEFAUNT_SECONDARY_ROAD_NETWORK_ROAD_LENGTH_DEVIATION = 4;
    private UndirectedGraph<Point2D, Line2D> graph;
    private Double sampleRadius;
    private Integer samplesPerStep;
    private Double deviationAngle;
    private Integer paramDegree;
    private Double connectivity;
    private Double roadSegmentLength;
    private Double snapSize;
    private TestCanvas canvas;
    private Integer numOfStartPoints;
    private Double secondaryRoadNetworkDeviationAngle;
    private Double secondaryRoadNetworkRoadLengthDeviation;

    public CityBuilder(UndirectedGraph<Point2D, Line2D> graph, TestCanvas canvas) {
        this.graph = graph;
        this.canvas = canvas;
    }

    @SuppressWarnings("unused")
    public CityBuilder withStartPointsPerCycle(int numOfStartPoints) {
        if (numOfStartPoints < 1) {
            throw new IllegalArgumentException("NumOfStartPoints must be at least 1");
        }
        this.numOfStartPoints = numOfStartPoints;
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
    public CityBuilder withRoadSegmentLength(double roadSegmentLength) {
        if (roadSegmentLength <= 0) {
            throw new IllegalArgumentException(
                    "roadSegmentLength must be > 0 (" + roadSegmentLength + " provided)"
            );
        }
        this.roadSegmentLength = roadSegmentLength;
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
    public CityBuilder withDeviationAngle(double deviationAngle) {
        if (deviationAngle < 0 || deviationAngle > Math.PI) {
            throw new IllegalArgumentException(
                    "Deviation angle must be >= 0 and <= Math.PI (" + deviationAngle + " provided)"
            );
        }
        this.deviationAngle = deviationAngle;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withSecondaryRoadNetworkDeviationAngle(double dAngle) {
        if (Math.abs(secondaryRoadNetworkDeviationAngle) >= Math.PI * 2) {
            throw new IllegalArgumentException("secondaryRoadNetworkDeviationAngle must be in [0; Math.PI*2)");
        }
        this.secondaryRoadNetworkDeviationAngle = dAngle;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withSecondaryRoadNetworkRoadLengthDeviation(double dLength) {
        if (Math.abs(dLength) >= roadSegmentLength) {
            throw new IllegalArgumentException("secondaryRoadNetworkRoadLengthDeviation can't be greater than " +
                    "roadSegmentLength (the former is " + secondaryRoadNetworkDeviationAngle + ", " +
                    "the latter is " + roadSegmentLength + ")");
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
    public CityBuilder withParamDegree(int paramDegree) {
        if (paramDegree < 2) {
            throw new IllegalArgumentException("paramDegree must be >= 2");
        }
        this.paramDegree = paramDegree;
        return this;
    }

    @SuppressWarnings("unused")
    public CityBuilder withDefaults() {
        sampleRadius = DEFAULT_SAMPLE_RADIUS;
        samplesPerStep = DEFAULT_SAMPLES_PER_STEP;
        paramDegree = DEFAULT_PARAM_DEGREE;
        connectivity = DEFAULT_CONNECTIVITY;
        roadSegmentLength = DEFAULT_ROAD_SEGMENT_LENGTH;
        snapSize = DEFAULT_SNAP_SIZE;
        deviationAngle = DEFAULT_DEVIATION_ANGLE;
        numOfStartPoints = DEFAULT_NUM_OF_START_POINTS;
        secondaryRoadNetworkDeviationAngle = DEFAULT_SECONDARY_ROAD_NETWORK_DEVIATION_ANGLE;
        secondaryRoadNetworkRoadLengthDeviation = DEFAUNT_SECONDARY_ROAD_NETWORK_ROAD_LENGTH_DEVIATION;
        return this;
    }

    @SuppressWarnings("unused")
    public City build() {
        if (graph == null) {
            throw new IllegalStateException("Graph not set");
        }
        if (canvas == null) {
            throw new IllegalStateException("Canvas not set");
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
        if (paramDegree == null) {
            throw new IllegalStateException("paramDegree not set");
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
        if (numOfStartPoints == null) {
            throw new IllegalStateException("numOfStartPoints not set");
        }
        if (secondaryRoadNetworkRoadLengthDeviation == null) {
            throw new IllegalStateException("secondaryRoadNetworkRoadLengthDeviation not set");
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
                new Random(10),
                paramDegree,
                connectivity,
                roadSegmentLength,
                snapSize,
                numOfStartPoints,
                secondaryRoadNetworkDeviationAngle,
                secondaryRoadNetworkRoadLengthDeviation
        );
    }

}
