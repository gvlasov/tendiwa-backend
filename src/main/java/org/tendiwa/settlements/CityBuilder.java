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
    private UndirectedGraph<Point2D, Line2D> graph;
    private Double sampleRadius;
    private Integer samplesPerStep;
    private Double deviationAngle;
    private Integer paramDegree;
    private Double connectivity;
    private Double roadSegmentLength;
    private Double snapSize;
    private TestCanvas canvas;
    private static final double DEFAULT_SAMPLE_RADIUS = 30.;

    public CityBuilder(UndirectedGraph<Point2D, Line2D> graph, TestCanvas canvas) {
        this.graph = graph;
        this.canvas = canvas;
    }

    public CityBuilder withSampleRadius(double sampleRadius) {
        if (sampleRadius <= 0) {
            throw new IllegalArgumentException("Sample radius must be > 0");
        }
        this.sampleRadius = sampleRadius;
        return this;
    }

    public CityBuilder withRoadSegmentLength(double roadSegmentLength) {
        if (roadSegmentLength <= 0) {
            throw new IllegalArgumentException(
                    "roadSegmentLength must be > 0 (" + roadSegmentLength + " provided)"
            );
        }
        this.roadSegmentLength = roadSegmentLength;
        return this;
    }

    public CityBuilder withSamplesPerStep(int samplesPerStep) {
        if (samplesPerStep <= 0) {
            throw new IllegalArgumentException("Samples per step must be > 0");
        }
        this.samplesPerStep = samplesPerStep;
        return this;
    }

    public CityBuilder withDeviationAngle(double deviationAngle) {
        if (deviationAngle < 0 || deviationAngle > Math.PI) {
            throw new IllegalArgumentException(
                    "Deviation angle must be >= 0 and <= Math.PI (" + deviationAngle + " provided)"
            );
        }
        this.deviationAngle = deviationAngle;
        return this;
    }

    public CityBuilder withConnectivity(double connectivity) {
        if (connectivity < 0 || connectivity > 1) {
            throw new IllegalArgumentException(
                    "Connectivity must be < 0 and > 1 (" + connectivity + " provided)"
            );
        }
        this.connectivity = connectivity;
        return this;
    }

    public CityBuilder withParamDegree(int paramDegree) {
        if (paramDegree < 2) {
            throw new IllegalArgumentException("paramDegree must be >= 2");
        }
        this.paramDegree = paramDegree;
        return this;
    }

    public CityBuilder withDefaults() {

        sampleRadius = DEFAULT_SAMPLE_RADIUS;
        samplesPerStep = DEFAULT_SAMPLES_PER_STEP;
        paramDegree = DEFAULT_PARAM_DEGREE;
        connectivity = DEFAULT_CONNECTIVITY;
        roadSegmentLength = DEFAULT_ROAD_SEGMENT_LENGTH;
        snapSize = DEFAULT_SNAP_SIZE;
        deviationAngle = DEFAULT_DEVIATION_ANGLE;
        return this;
    }

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
        final Random random = new Random(2);
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
                canvas
        );
    }

}
