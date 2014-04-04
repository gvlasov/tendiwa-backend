package org.tendiwa.settlements;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.drawing.DrawingLine;
import org.tendiwa.drawing.DrawingPoint;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.MinimalCycle;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [Kelly section 4.3.1]
 * <p>
 * A part of a city bounded by a fundamental basis cycle (one of those in <i>minimal cycle basis</i> from [Kelly section
 * 4.3.1, figure 41].
 */
public class CityCell {
    private final SimpleGraph<Point2D, Line2D> relevantNetwork;
    private final SimpleGraph<Point2D, Line2D> secRoadNetwork;
    private Collection<Line2D> filamentEdges;
    /**
     * [Kelly figure 42]
     * <p>
     */
    private final int paramDegree;
    private final double roadSegmentLength;
    /**
     * Coordinates of cycle's vertices sorted in a clockwise or counter-clockwise order.
     */
    private final Coordinate[] ring;
    /**
     * Order of sorting of {@link #ring}.
     */
    private final boolean isCycleClockwise;
    private final double snapSize;
    private final double connectivity;
    private double secondaryRoadNetworkDeviationAngle;
    private final Random random;
    private final TestCanvas canvas;
    private Collection<Point2D> deadEnds = new HashSet<>();
    private final int numOfStartPoints;

    /**
     * @param graph
     *         A preconstructed graph of low level roads.
     * @param filamentEdges
     * @param paramDegree
     * @param snapSize
     * @param connectivity
     *         How likely it is to snap to node or road when possible. When connectivity == 1.0, algorithm will always snap when
     *         possible. When connectivity == 0.0, algorithm will never snap.
     * @param random
     *         A seeded {@link java.util.Random} used to generate the parent {@link org.tendiwa.settlements.City}.
     */
    CityCell(
            SimpleGraph<Point2D, Line2D> graph,
            MinimalCycle<Point2D, Line2D> cycle,
            Collection<Line2D> filamentEdges,
            int paramDegree,
            double roadSegmentLength,
            double snapSize,
            double connectivity,
            double secondaryRoadNetworkDeviationAngle,
            int numOfStartPoints,
            Random random,
            TestCanvas canvas
    ) {
        this.filamentEdges = filamentEdges;
        this.paramDegree = paramDegree;
        this.roadSegmentLength = roadSegmentLength;
        this.snapSize = snapSize;
        this.connectivity = connectivity;
        this.secondaryRoadNetworkDeviationAngle = secondaryRoadNetworkDeviationAngle;
        this.random = random;
        this.canvas = canvas;
        this.numOfStartPoints = numOfStartPoints;

        relevantNetwork = graph;
        secRoadNetwork = new SimpleGraph<>(graph.getEdgeFactory());

        for (Point2D vertex : relevantNetwork.vertexSet()) {
            deadEnds.add(vertex);
        }


        ring = pointListToCoordinateArray(cycle.vertexList());
        // TODO: Are all cycles counter-clockwise? (because of the MCB algorithm)
        isCycleClockwise = determineCycleDirection(ring);

        buildLine2DNetwork(cycle);
    }

    /**
     * Transforms a list of {@link org.tendiwa.geometry.Point2D}s to an array of {@link com.vividsolutions.jts.geom.Coordinate}s.
     *
     * @param points
     *         A list of points.
     * @return An array of coordinates.
     */
    private Coordinate[] pointListToCoordinateArray(List<Point2D> points) {
        List<Coordinate> collect = points.stream()
                .map(a -> new Coordinate(a.x, a.y))
                .collect(Collectors.toList());
        collect.add(new Coordinate(points.get(0).x, points.get(0).y));
        return collect.toArray(new Coordinate[points.size()]);
    }

    /**
     * @param ring
     * @return
     */
    private boolean determineCycleDirection(Coordinate[] ring) {
        return !CGAlgorithms.isCCW(ring);
    }

    /**
     * [Kelly figure 42]
     * <p>
     * Calculates initial road segments and processes road growth.
     *
     * @param cycle
     */
    private void buildLine2DNetwork(MinimalCycle<Point2D, Line2D> cycle) {
        Deque<Line2DNetworkStep> nodeQueue = new ArrayDeque<>();
        for (Line2D road : longestRoads(cycle)) {
            // Source node is the same as midpoint from [Kelly figure 42], since in this implementation points are inherently nodes.
            Point2D sourceNode = calculateDeviatedMidPoint(road);
            insertNode(road, sourceNode);
            double direction;
//            try {
            direction = deviatedBoundaryPerpendicular(sourceNode, road);
//            } catch (RuntimeException e) {
//                return;
//            }
            Point2D newNode = tryPlacingRoad(sourceNode, direction);
            if (newNode != null) {
                nodeQueue.push(new Line2DNetworkStep(newNode, direction));
            }
        }
        int iter = 0;
        while (!nodeQueue.isEmpty()) {
            iter++;
//            if (iter == 71) {
//                break;
//            }
            Line2DNetworkStep node = nodeQueue.pop();
            for (int i = 1; i < paramDegree; i++) {
                double newDirection = deviateDirection(node.direction + Math.PI + i * (Math.PI * 2 / paramDegree));
                Point2D newNode = tryPlacingRoad(node.node, newDirection);
                if (newNode != null && !isDeadEnd(newNode)) {
                    nodeQueue.push(new Line2DNetworkStep(newNode, newDirection));
                }
            }
        }
    }

    private boolean isDeadEnd(Point2D node) {
        return deadEnds.contains(node);
    }

    private double deviateDirection(double newDirection) {
        return newDirection - secondaryRoadNetworkDeviationAngle + random.nextDouble() * secondaryRoadNetworkDeviationAngle * 2;
    }

    private double deviatedLength(double roadSegmentLength) {
        return roadSegmentLength;
    }

    /**
     * [Kelly figure 42]
     *
     * @param deviatedMidpoint
     * @return
     */
    private double deviatedBoundaryPerpendicular(Point2D deviatedMidpoint, Line2D edge) {
        double angle = edge.start.angleTo(edge.end);
        return angle + Math.PI / 2
                * (isCycleClockwise ? -1 : 1)
                * (isStartBeforeEndInRing(new Coordinate(edge.start.x, edge.start.y), new Coordinate(edge.end.x, edge.end.y)) ? 1 : -1);
    }

    private boolean isStartBeforeEndInRing(Coordinate start, Coordinate end) {
        for (int i = 0; i < ring.length; i++) {
            if (ring[i].equals(start)) {
                if (ring[i + 1].equals(end)) {
                    return true;
                } else {
                    assert ring[i - 1].equals(end);
                    return false;
                }
            }
        }
        throw new RuntimeException(start + " is not before or after " + end);
    }

    /**
     * @param vertices
     *         A list of vertices where the first one is equal to the last one.
     * @return
     */
    private Coordinate[] buildNodeRing(List<Point2D> vertices) {
        assert vertices.get(0).equals(vertices.get(vertices.size() - 1));
        Point2D sourceNode = vertices.get(0);
        Point2D currentNode = sourceNode;
        Point2D previousNode = null;
        Coordinate[] ring = new Coordinate[vertices.size()];
        int i = 0;
        // Traverses all vertices starting from sourceNode until it comes back to sourceNode.
        do {
            Point2D nextNode = null;
            // Having all edges of degree 2 proves that this is a cycle.
            assert relevantNetwork.edgesOf(currentNode).size() == 2 : relevantNetwork.edgesOf(currentNode).size();
            for (Line2D edge : relevantNetwork.edgesOf(currentNode)) {
                if (previousNode == null) {
                    if (relevantNetwork.getEdgeSource(edge) == sourceNode) {
                        nextNode = relevantNetwork.getEdgeTarget(edge);
                    } else {
                        assert relevantNetwork.getEdgeTarget(edge) == sourceNode :
                                relevantNetwork.getEdgeTarget(edge)
                                        + "\n" + relevantNetwork.getEdgeSource(edge)
                                        + "\n" + sourceNode;
                        nextNode = relevantNetwork.getEdgeSource(edge);
                    }
                    break;
                } else {
                    Point2D edgeTarget = relevantNetwork.getEdgeTarget(edge);
                    if (edgeTarget == previousNode) {
                        continue;
                    }
                    Point2D edgeSource = relevantNetwork.getEdgeSource(edge);
                    if (edgeSource == previousNode) {
                        continue;
                    }
                    if (currentNode == edgeSource) {
                        nextNode = edgeTarget;
                    } else {
                        assert currentNode == edgeTarget :
                                currentNode + "\n"
                                        + edgeSource + "\n"
                                        + edgeTarget + "\n"
                                        + (currentNode == edgeSource);
                        nextNode = edgeSource;
                    }
                }
            }
            assert nextNode != null;
            ring[i] = new Coordinate(currentNode.x, currentNode.y);
            i++;
            previousNode = currentNode;
            currentNode = nextNode;
        } while (currentNode != sourceNode);
        ring[i] = ring[0];
        assert i == relevantNetwork.vertexSet().size() : i + " " + relevantNetwork.vertexSet().size();
        return ring;
    }

    /**
     * [Kelly figure 42, function placeSegment]
     * <p>
     * Tries adding a new road to the secondary road network graph.
     *
     * @param sourceNode
     *         Start node of a new road.
     * @param direction
     *         Angle of a road to x-axis.
     * @return The new node, or null if placing did not succeed.
     */
    private Point2D tryPlacingRoad(Point2D sourceNode, double direction) {
        double roadLength = deviatedLength(roadSegmentLength);
        double dx = roadLength * Math.cos(direction);
        double dy = roadLength * Math.sin(direction);
        Point2D targetNode = new Point2D(sourceNode.x + dx, sourceNode.y + dy);
        SnapEvent snapEvent = new SnapTest(snapSize, sourceNode, targetNode, relevantNetwork, canvas).snap();
        if (sourceNode.equals(snapEvent.targetNode)) {
            assert false;
        }
        switch (snapEvent.eventType) {
            case NO_SNAP:
                if (!relevantNetwork.addVertex(targetNode)) {
                    assert false;
                    return null;
                }
                addRoad(sourceNode, targetNode);
                drawPoint(snapEvent.targetNode, Color.CYAN, 5);
                return snapEvent.targetNode;
            case ROAD_SNAP:
                if (random.nextDouble() < connectivity) {
                    Point2D newNode = snapEvent.targetNode;
                    insertNode(snapEvent.road, newNode);
//                    System.out.println(sourceNode + " " + newNode + " " + sourceNode.equals(newNode) + " " + (sourceNode == newNode));
                    addRoad(sourceNode, newNode);
                    drawPoint(snapEvent.targetNode, Color.YELLOW, 5);
                    if (!filamentEdges.contains(snapEvent.road)) {
                        deadEnds.add(snapEvent.targetNode);
                    }
                    return snapEvent.targetNode;
                } else {
                    return null;
                }
            case NODE_SNAP:
                if (random.nextDouble() < connectivity) {
                    if (isDeadEnd(snapEvent.targetNode) && isDeadEnd(sourceNode)) {
                        return null;
                    }
                    addRoad(sourceNode, snapEvent.targetNode);
                    drawPoint(snapEvent.targetNode, Color.WHITE, 5);
                    return null;
                } else {
                    return null;
                }
            case NO_NODE:
                return null;
            default:
                throw new RuntimeException();
        }
    }

    private void addRoad(Point2D sourceNode, Point2D targetNode) {
        if (
                Math.abs(sourceNode.x - 131.17) < 1
                        && Math.abs(sourceNode.y - 214.18) < 1
                        && Math.abs(targetNode.x - 138.43) < 1
                        && Math.abs(targetNode.y - 202.02) < 1
                        || Math.abs(targetNode.x - 131.17) < 1
                        && Math.abs(targetNode.y - 214.18) < 1
                        && Math.abs(sourceNode.x - 138.43) < 1
                        && Math.abs(sourceNode.y - 202.02) < 1
                ) {
            System.out.println(2);
        }
        relevantNetwork.addEdge(sourceNode, targetNode);
        secRoadNetwork.addVertex(sourceNode);
        secRoadNetwork.addVertex(targetNode);
        secRoadNetwork.addEdge(sourceNode, targetNode);
    }

    public UndirectedGraph<Point2D, Line2D> secondaryRoadNetwork() {
        return new UnmodifiableUndirectedGraph<>(secRoadNetwork);
    }

    private void drawPoint(Point2D point, Color color, double size) {
//        canvas.draw(point, DrawingPoint.withColorAndSize(color, size));
    }


    /**
     * [Kelly figure 42]
     * <p>
     * Adds new node between two existing nodes, removing an existing road between them and placing 2 new roads. to road
     * network. Since {@link org.tendiwa.settlements.RoadGraph} is immutable, new nodes are saved in a separate collection.
     *
     * @param road
     *         A road from {@link #relevantNetwork} on which a node is being inserted.
     * @param point
     *         A node on that road where the node resides.
     */
    private void insertNode(Line2D road, Point2D point) {
        assert !road.start.equals(point) : "point is start";
        assert !road.end.equals(point) : "point is end";
        assert road.start.distanceTo(point) > 0.1 : road.start.distanceTo(point) + " " + road.start.distanceTo(road.end);
        assert road.end.distanceTo(point) > 0.1 : road.end.distanceTo(point) + " " + road.start.distanceTo(road.end);
        relevantNetwork.removeEdge(road);
        relevantNetwork.addVertex(point);
        addRoad(road.start, point);
        addRoad(point, road.end);
    }

    private Point2D calculateDeviatedMidPoint(Line2D road) {
        return new Point2D(
                road.start.x + (road.end.x - road.start.x) / 2,
                road.start.y + (road.end.y - road.start.y) / 2
        );
    }

    /**
     * [Kelly figure 42]
     * <p>
     * Returns several of the longest roads.
     *
     * @param cycle
     * @return Several of the longest roads.
     */
    private Collection<Line2D> longestRoads(MinimalCycle<Point2D, Line2D> cycle) {
        List<Line2D> edges = Lists.newArrayList(cycle);
        Collections.sort(
                edges,
                // TODO: The fuck is signum doing here?
                (o1, o2) -> (int) Math.signum(o2.start.distanceTo(o2.end) - o1.start.distanceTo(o1.end))
        );
        return edges.subList(0, numOfStartPoints);
    }

    class Line2DNetworkStep {
        private final Point2D node;
        private final double direction;

        Line2DNetworkStep(Point2D node, double direction) {
            this.node = node;
            this.direction = direction;
        }
    }
}
