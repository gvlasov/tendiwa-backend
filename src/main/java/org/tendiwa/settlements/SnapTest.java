package org.tendiwa.settlements;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.meta.Range;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SnapTest {
    private static final GeometryFactory factory = new GeometryFactory();
    private final double snapSize;
    private final SecondaryRoadNetworkNode sourceNode;
    private final SecondaryRoadNetworkNode targetNode;
    private final SimpleGraph<SecondaryRoadNetworkNode, SecondaryRoad> roadCycle;
    private double minR = Double.MAX_VALUE;

    SnapTest(
            double snapSize,
            SecondaryRoadNetworkNode sourceNode,
            SecondaryRoadNetworkNode targetNode,
            SimpleGraph<SecondaryRoadNetworkNode, SecondaryRoad> roadCycle
    ) {
        this.snapSize = snapSize;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.roadCycle = roadCycle;
    }

    SnapEvent snap() {
        Collection<SecondaryRoad> roadsToTest = findSegmentsToTest(sourceNode, targetNode, snapSize);

        SecondaryRoadNetworkNode snapNode = null;
        Set<SecondaryRoadNetworkNode> verticesToTest = new HashSet<>();
        for (SecondaryRoad segment : roadsToTest) {
            // Individual vertices will be added only once
            if (segment.start != sourceNode && segment.end != sourceNode) {
                assert !segment.start.equals(sourceNode);
                assert !segment.end.equals(sourceNode);
                verticesToTest.add(segment.start);
                verticesToTest.add(segment.end);
            }
        }
        for (SecondaryRoadNetworkNode vertex : verticesToTest) {
            NodePosition nodePosition = nodeProximityTest(sourceNode, targetNode, vertex);
            if (nodePosition.r < minR && nodePosition.distance <= snapSize) {
                minR = nodePosition.r;
                snapNode = vertex;
            }
        }
        if (snapNode != null) {
            System.out.println(1);
            System.out.println("snap " + sourceNode + " to " + snapNode);
            return new SnapEvent(snapNode, SnapEventType.NODE_SNAP, null);
        }
        minR = Double.MAX_VALUE;
        SnapEvent snapEvent = null;
        for (SecondaryRoad road : roadsToTest) {
            if (road.start == sourceNode || road.end == sourceNode) {
                continue;
            }
            if (isSegmentIntersectionProbable(sourceNode.point, targetNode.point, road.start.point, road.end.point)) {
                LineIntersection intersection = new LineIntersection(
                        sourceNode.point,
                        targetNode.point,
                        new Line2D(road.start.point, road.end.point)
                );
                if (intersection.r >= minR) {
                    continue;
                }
                minR = intersection.r;
                if (intersection.intersects) {
                    Point2D intersectionPoint = intersection.getIntersectionPoint(sourceNode.point, targetNode.point);
                    snapEvent = new SnapEvent(
                            new SecondaryRoadNetworkNode(intersectionPoint, isRoadDead(road)),
                            SnapEventType.ROAD_SNAP,
                            road
                    );
                    System.out.println(2);
                    System.out.println(sourceNode);
                    System.out.println(targetNode);
                    System.out.println(new NodePosition(sourceNode.point, targetNode.point, snapEvent.road.start.point).distance);
                    System.out.println(snapEvent.road.start);
                    System.out.println(snapEvent.road.end);
                    System.out.println(intersectionPoint);
                }
            }
        }
        if (snapEvent != null) {
            return snapEvent;
        }

        for (SecondaryRoad road : roadsToTest) {
//		if (road.end.point.distanceTo(targetNode.point) < snapSize) {
            if (road.start == sourceNode || road.end == sourceNode) {
                continue;
            }
            NodePosition nodePosition = new NodePosition(
                    road.start.point,
                    road.end.point,
                    targetNode.point
            );
            if (nodePosition.distance > snapSize) {
                continue;
            }
            System.out.println(3);
            System.out.println(nodePosition.r);
            System.out.println(road.start);
            System.out.println(road.end);
            System.out.println(sourceNode);
            System.out.println(targetNode);
            return new SnapEvent(
                    new SecondaryRoadNetworkNode(
                            new Point2D(
                                    road.start.point.x + nodePosition.r * (road.end.point.x - road.start.point.x),
                                    road.start.point.y + nodePosition.r * (road.end.point.y - road.start.point.y)
                            ),
                            isRoadDead(road)
                    ),
                    SnapEventType.ROAD_SNAP,
                    road
            );
//		}
        }
        System.out.println(0);
        return new SnapEvent(targetNode, SnapEventType.NO_SNAP, null);
    }

    private boolean isRoadDead(SecondaryRoad segment) {
        return segment.start.isDeadEnd && segment.end.isDeadEnd;
    }

    /**
     * [Kelly 4.3.3.4]
     * <p/>
     *
     * @param abStart
     * @param abEnd
     * @param cdStart
     * @param cdEnd
     * @return
     */
    private boolean isSegmentIntersectionProbable(Point2D abStart, Point2D abEnd, Point2D cdStart, Point2D cdEnd) {
        NodePosition nodePosition = new NodePosition(abStart, abEnd, cdStart);
        NodePosition nodePosition2 = new NodePosition(abStart, abEnd, cdEnd);
        if (Math.signum(nodePosition.s) == Math.signum(nodePosition2.s)) {
            return false;
        }
        if (!(Range.contains(0, 1, nodePosition.r) && Range.contains(0, 1, nodePosition2.r)
                || nodePosition.r < 0 && nodePosition2.r > 1 || nodePosition.r > 1 && nodePosition2.r < 0)
                ) {
            return false;
        }
        return true;
    }

    /**
     * [Kelly 4.3.3.3]
     * <p/>
     *
     * @param a
     * @param b
     * @param p
     * @return
     */
    private NodePosition nodeProximityTest(SecondaryRoadNetworkNode a, SecondaryRoadNetworkNode b, SecondaryRoadNetworkNode p) {
        NodePosition nodePosition = new NodePosition(a.point, b.point, p.point);
        return nodePosition;
    }

    /**
     * [Kelly figure 46]
     *
     * @param sourceNode
     * @param targetPoint
     * @param snapSize
     * @return
     */
    private Collection<SecondaryRoad> findSegmentsToTest(SecondaryRoadNetworkNode sourceNode, SecondaryRoadNetworkNode targetPoint, double snapSize) {
        // TODO: Optimize culling
        double minX = Math.min(sourceNode.point.x, targetPoint.point.x) - snapSize;
        double minY = Math.min(sourceNode.point.y, targetPoint.point.y) - snapSize;
        double maxX = Math.max(sourceNode.point.x, targetPoint.point.x) + snapSize;
        double maxY = Math.max(sourceNode.point.y, targetPoint.point.y) + snapSize;
        Geometry boundingBox = factory.createLineString(new Coordinate[]{
                new Coordinate(minX, minY),
                new Coordinate(maxX, maxY)
        }).getEnvelope();
        Collection<SecondaryRoad> answer = new LinkedList<>();
        for (SecondaryRoad edge : roadCycle.edgeSet()) {
            LineString edgeLine = factory.createLineString(new Coordinate[]{
                    new Coordinate(edge.start.point.x, edge.start.point.y),
                    new Coordinate(edge.end.point.x, edge.end.point.y)
            });
            if (edgeLine.getEnvelope().intersects(boundingBox)) {
                answer.add(edge);
            }
        }
        return answer;
    }
}
