package org.tendiwa.settlements;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.DrawingCell;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Point2D;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * [Kelly section 4.3.1]
 * <p/>
 * A part of a city bounded by a fundamental basis cycle (one of those in <i>minimal cycle basis</i> from [Kelly section
 * 4.3.1, figure 41].
 */
public class CityCell {
    public final SimpleGraph<SecondaryRoadNetworkNode, SecondaryRoad> secRoadNetwork;
    /**
     * [Kelly figure 42]
     * <p/>
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
    private final Random random;
    private final TestCanvas canvas;

    /**
     * @param vertices     Vertices forming a single cycle.
     * @param paramDegree
     * @param snapSize
     * @param connectivity How likely it is to snap to node or road when possible. When connectivity == 1.0, algorithm will always snap when
     *                     possible. When connectivity == 0.0, algorithm will never snap.
     * @param random       A seeded {@link java.util.Random} used to generate the parent {@link City}.
     */
    CityCell(List<Point2D> vertices, int paramDegree, double roadSegmentLength, double snapSize, double connectivity, Random random, TestCanvas canvas) {
        this.paramDegree = paramDegree;
        this.roadSegmentLength = roadSegmentLength;
        this.snapSize = snapSize;
        this.connectivity = connectivity;
        this.random = random;
        this.canvas = canvas;

        secRoadNetwork = new SimpleGraph<>(new EdgeFactory<SecondaryRoadNetworkNode, SecondaryRoad>() {
            @Override
            public SecondaryRoad createEdge(SecondaryRoadNetworkNode sourceVertex, SecondaryRoadNetworkNode targetVertex) {
                return new SecondaryRoad(sourceVertex, targetVertex);
            }
        });
        List<SecondaryRoadNetworkNode> nodes = new LinkedList<>();
        for (Point2D vertex : vertices) {
            SecondaryRoadNetworkNode node = new SecondaryRoadNetworkNode(vertex, true);
            secRoadNetwork.addVertex(node);
            nodes.add(node);
        }

        Collection<SecondaryRoad> edges = new ArrayList<>(vertices.size());
        int l = vertices.size() - 1;
        for (int i = 0; i < l; i++) {
            edges.add(
                    new SecondaryRoad(
                            nodes.get(i),
                            nodes.get(i + 1)
                    )
            );
        }
        edges.add(
                new SecondaryRoad(
                        nodes.get(l),
                        nodes.get(0)
                )
        );

        for (SecondaryRoad edge : edges) {
            secRoadNetwork.addEdge(edge.start, edge.end, edge);
        }
        ring = buildNodeRing(nodes);
        isCycleClockwise = determineCycleDirection(ring);

        assert new ConnectivityInspector<>(secRoadNetwork).isGraphConnected();
        for (SecondaryRoadNetworkNode vertex : secRoadNetwork.vertexSet()) {
            assert secRoadNetwork.degreeOf(vertex) == 2;
        }
        buildSecondaryRoadNetwork();
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
     * <p/>
     * Calculates initial road segments and processes road growth.
     */
    private void buildSecondaryRoadNetwork() {
        Deque<SecondaryRoadNetworkStep> nodeQueue = new ArrayDeque<>();
        for (SecondaryRoad road : longestRoads()) {
            // Source node is the same as midpoint from [Kelly figure 42], since in this implementation points are inherently nodes.
            SecondaryRoadNetworkNode sourceNode = calculateDeviatedMidPoint(road);
            insertNode(road, sourceNode);
            double direction = deviatedBoundaryPerpendicular(sourceNode, road);
            SecondaryRoadNetworkNode newNode = tryPlacingRoad(sourceNode, direction);
            if (newNode != null) {
                nodeQueue.push(new SecondaryRoadNetworkStep(newNode, direction));
            }
        }
        int iter = 0;
        while (!nodeQueue.isEmpty()) {
            if (iter++ == 9) {
                break;
            }
            SecondaryRoadNetworkStep node = nodeQueue.pop();
            for (int i = 1; i < paramDegree; i++) {
                double newDirection = deviateDirection(node.direction + Math.PI + i * (Math.PI * 2 / paramDegree));
                SecondaryRoadNetworkNode newNode = tryPlacingRoad(node.node, newDirection);
                if (newNode != null && !newNode.isDeadEnd) {
                    nodeQueue.push(new SecondaryRoadNetworkStep(newNode, newDirection));
                }
            }
        }
    }

    private double deviateDirection(double newDirection) {
        return newDirection;
    }

    /**
     * [Kelly figure 42]
     *
     * @param deviatedMidpoint
     * @return
     */
    private double deviatedBoundaryPerpendicular(SecondaryRoadNetworkNode deviatedMidpoint, SecondaryRoad edge) {
        double angle = edge.start.point.angleTo(edge.end.point);
        return angle + Math.PI / 2
                * (isCycleClockwise ? -1 : 1)
                * (isStartBeforeEndInRing(new Coordinate(edge.start.point.x, edge.start.point.y), new Coordinate(edge.end.point.x, edge.end.point.y)) ? 1 : -1);
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
        throw new RuntimeException();
    }

    private Coordinate[] buildNodeRing(List<SecondaryRoadNetworkNode> vertices) {

        SecondaryRoadNetworkNode sourceNode = vertices.get(0);
        SecondaryRoadNetworkNode currentNode = sourceNode;
        SecondaryRoadNetworkNode previousNode = null;
        Coordinate[] ring = new Coordinate[secRoadNetwork.vertexSet().size() + 1];
        int i = 0;
        // Traverses all vertices starting from sourceNode until it comes back to sourceNode.
        do {
            SecondaryRoadNetworkNode nextNode = null;
            // Having all edges of degree 2 proves that this is a cycle.
            assert secRoadNetwork.edgesOf(currentNode).size() == 2 : secRoadNetwork.edgesOf(currentNode).size();
            for (SecondaryRoad edge : secRoadNetwork.edgesOf(currentNode)) {
                if (previousNode == null) {
                    if (secRoadNetwork.getEdgeSource(edge) == sourceNode) {
                        nextNode = secRoadNetwork.getEdgeTarget(edge);
                    } else {
                        assert secRoadNetwork.getEdgeTarget(edge) == sourceNode :
                                secRoadNetwork.getEdgeTarget(edge)
                                        + "\n" + secRoadNetwork.getEdgeSource(edge)
                                        + "\n" + sourceNode;
                        nextNode = secRoadNetwork.getEdgeSource(edge);
                    }
                    break;
                } else {
                    SecondaryRoadNetworkNode edgeTarget = secRoadNetwork.getEdgeTarget(edge);
                    if (edgeTarget == previousNode) {
                        continue;
                    }
                    SecondaryRoadNetworkNode edgeSource = secRoadNetwork.getEdgeSource(edge);
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
            ring[i] = new Coordinate(currentNode.point.x, currentNode.point.y);
            i++;
            previousNode = currentNode;
            currentNode = nextNode;
        } while (currentNode != sourceNode);
        ring[i] = ring[0];
        assert i == secRoadNetwork.vertexSet().size() : i + " " + secRoadNetwork.vertexSet().size();
        return ring;
    }

    /**
     * [Kelly figure 42, function placeSegment]
     * <p/>
     * Tries adding a new road to the secondary road network graph.
     *
     * @param sourceNode Start node of a new road.
     * @param direction  Angle of a road to x-axis.
     * @return The new node, or null if placing did not succeed.
     */
    private SecondaryRoadNetworkNode tryPlacingRoad(SecondaryRoadNetworkNode sourceNode, double direction) {
        double roadLength = deviatedLength(this.roadSegmentLength);
        double dx = roadLength * Math.cos(direction);
        double dy = roadLength * Math.sin(direction);
        SecondaryRoadNetworkNode targetNode = new SecondaryRoadNetworkNode(
                new Point2D(sourceNode.point.x + dx, sourceNode.point.y + dy),
                false
        );
        SnapEvent snapEvent = new SnapTest(snapSize, sourceNode, targetNode, secRoadNetwork, canvas).snap();
        assert !sourceNode.equals(snapEvent.targetNode);
        switch (snapEvent.eventType) {
            case NO_SNAP:
                if (!secRoadNetwork.addVertex(targetNode)) {
                    return null;
                }
                secRoadNetwork.addEdge(sourceNode, targetNode);
                drawPoint(targetNode.point, Color.CYAN, 5);
                return snapEvent.targetNode;
            case ROAD_SNAP:
                if (random.nextDouble() < connectivity) {
                    SecondaryRoadNetworkNode newNode = snapEvent.targetNode;
                    insertNode(snapEvent.road, newNode);
                    secRoadNetwork.addEdge(sourceNode, newNode);
//				drawPoint(snapEvent.targetNode.point, Color.CYAN, 10);
                    return snapEvent.targetNode;
                } else {
                    return null;
                }
            case NODE_SNAP:
                if (random.nextDouble() < connectivity) {
                    if (snapEvent.targetNode.isDeadEnd && sourceNode.isDeadEnd) {
                        return null;
                    }
//                    secRoadNetwork.addVertex(snapEvent.targetNode);
                    secRoadNetwork.addEdge(sourceNode, snapEvent.targetNode);
//				drawPoint(snapEvent.targetNode.point, Color.ORANGE, 10);
                    return null;
                } else {
                    return null;
                }
            default:
                throw new RuntimeException();
        }
    }

    private void drawPoint(Point2D point, Color color, double size) {
        canvas.draw(new Cell(
                (int) Math.round(point.x),
                (int) Math.round(point.y)
        ), DrawingCell.withColorAndSize(color, size));
    }

    private double deviatedLength(double roadSegmentLength) {
        return roadSegmentLength;
    }

    /**
     * [Kelly figure 42]
     * <p/>
     * Adds new node between two existing nodes, removing an existing road between them and placing 2 new roads. to road
     * network. Since {@link org.tendiwa.settlements.RoadGraph} is immutable, new nodes are saved in a separate collection.
     *
     * @param road  A road from {@link #secRoadNetwork} on which a node is being inserted.
     * @param point A node on that road where the node resides.
     */
    private void insertNode(SecondaryRoad road, SecondaryRoadNetworkNode point) {
        assert !road.start.equals(point) : "point is start";
        assert !road.end.equals(point) : "point is end";
        assert road.start.point.distanceTo(point.point) > 0.1 : road.start.point.distanceTo(point.point) + " " + road.start.point.distanceTo(road.end.point);
        assert road.end.point.distanceTo(point.point) > 0.1 : road.end.point.distanceTo(point.point) + " " + road.start.point.distanceTo(road.end.point);
        secRoadNetwork.removeEdge(road);
        secRoadNetwork.addVertex(point);
        secRoadNetwork.addEdge(road.start, point);
        secRoadNetwork.addEdge(point, road.end);
    }

    private SecondaryRoadNetworkNode calculateDeviatedMidPoint(SecondaryRoad road) {
        return new SecondaryRoadNetworkNode(
                new Point2D(
                        road.start.point.x + (road.end.point.x - road.start.point.x) / 2,
                        road.start.point.y + (road.end.point.y - road.start.point.y) / 2
                ),
                true
        );
    }

    /**
     * [Kelly figure 42]
     * <p/>
     * Returns several of the longest roads.
     *
     * @return Several of the longest roads.
     */
    private Collection<SecondaryRoad> longestRoads() {
        List<SecondaryRoad> edges = new ArrayList<>(secRoadNetwork.edgeSet());
        Collections.sort(edges, new Comparator<SecondaryRoad>() {
            @Override
            public int compare(SecondaryRoad o1, SecondaryRoad o2) {
                return (int) Math.signum(o2.start.point.distanceTo(o2.end.point) - o1.start.point.distanceTo(o1.end.point));
            }
        });
        return edges.subList(0, 2);
    }

    class SecondaryRoadNetworkStep {
        private final SecondaryRoadNetworkNode node;
        private final double direction;

        SecondaryRoadNetworkStep(SecondaryRoadNetworkNode node, double direction) {
            this.node = node;
            this.direction = direction;
        }
    }
}
