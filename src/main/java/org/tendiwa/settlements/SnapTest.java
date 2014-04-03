package org.tendiwa.settlements;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.meta.Range;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingLine;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SnapTest {
    private static final GeometryFactory factory = new GeometryFactory();
    private final double snapSize;
    private final Point2D sourceNode;
    private Point2D targetNode;
    private final SimpleGraph<Point2D, Line2D> roadCycle;
    private TestCanvas canvas;
    private double minR;

    SnapTest(
            double snapSize,
            Point2D sourceNode,
            Point2D targetNode,
            SimpleGraph<Point2D, Line2D> roadCycle,
            TestCanvas canvas) {
        this.snapSize = snapSize;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.roadCycle = roadCycle;
        this.canvas = canvas;
        setTargetNode(targetNode);
        minR = 1 + snapSize / sourceNode.distanceTo(targetNode);
    }

    private void setTargetNode(Point2D node) {
        targetNode = node;
        minR = 1;
    }

    SnapEvent snap() {
        Collection<Line2D> roadsToTest = findSegmentsToTest(sourceNode, targetNode, snapSize);
        Point2D snapNode = null;
        Set<Point2D> verticesToTest = new HashSet<>();
        for (Line2D segment : roadsToTest) {
            // Individual vertices will be added only once
            if (segment.start != sourceNode && segment.end != sourceNode) {
                assert !segment.start.equals(sourceNode);
                assert !segment.end.equals(sourceNode);
                verticesToTest.add(segment.start);
                verticesToTest.add(segment.end);
            }
        }
        for (Point2D vertex : verticesToTest) {
            NodePosition nodePosition = new NodePosition(sourceNode, targetNode, vertex);
            if (nodePosition.r < minR && nodePosition.r >= 0 && nodePosition.distance <= snapSize) {
                minR = nodePosition.r;
                snapNode = vertex;
            }
        }
        SnapEvent snapEvent = null;
        if (snapNode != null) {
            snapEvent = new SnapEvent(snapNode, SnapEventType.NODE_SNAP, null);
            setTargetNode(snapNode);
        }
        for (Line2D road : roadsToTest) {
            if (road.start == sourceNode || road.end == sourceNode || road.start == targetNode || road.end == targetNode) {
                continue;
//                return new SnapEvent(null, SnapEventType.NO_NODE, null);
            }
            if (isSegmentIntersectionProbable(sourceNode, targetNode, road.start, road.end)) {
                LineIntersection intersection = new LineIntersection(
                        sourceNode,
                        targetNode,
                        new Line2D(road.start, road.end)
                );
                if (intersection.r >= minR || intersection.r < 0) {
                    continue;
                }
                if (intersection.intersects) {
                    Point2D intersectionPoint = intersection.getIntersectionPoint(sourceNode, targetNode);
                    System.out.println(
                            sourceNode + " "
                                    + targetNode + " "
                                    + road.start + " "
                                    + road.end
                    );
                    assert !intersectionPoint.equals(sourceNode);
                    snapEvent = new SnapEvent(
                            intersectionPoint,
                            SnapEventType.ROAD_SNAP,
                            road
                    );
                    minR = intersection.r;
                }
            }
        }
        if (snapEvent != null) {
            return snapEvent;
        }

        for (Line2D road : roadsToTest) {
            if (road.start == sourceNode || road.end == sourceNode) {
                continue;
            }
            NodePosition nodePosition = new NodePosition(
                    road.start,
                    road.end,
                    targetNode
            );
            if (nodePosition.distance > snapSize) {
                continue;
            }
            Point2D targetPoint = new Point2D(
                    road.start.x + nodePosition.r * (road.end.x - road.start.x),
                    road.start.y + nodePosition.r * (road.end.y - road.start.y)
            );
            assert !targetPoint.equals(sourceNode);
            return new SnapEvent(
                    targetPoint,
                    SnapEventType.ROAD_SNAP,
                    road
            );
        }
        return new SnapEvent(targetNode, SnapEventType.NO_SNAP, null);
    }

    /**
     * [Kelly 4.3.3.4]
     * <p>
     * In [Kelly 4.3.3.4] there is no pseudocode for this function, it is described in the second paragraph.
     * <p>
     * Provides a quick heuristic to see if two lines should be tested for an intersection.
     *
     * @param abStart
     *         Start of line ab.
     * @param abEnd
     *         End of line ab.
     * @param cdStart
     *         Start of line cd.
     * @param cdEnd
     *         End of line cd. Interchanging arguments for ab and cd should yield the same result.
     * @return true if it is possible
     */
    private boolean isSegmentIntersectionProbable(Point2D abStart, Point2D abEnd, Point2D cdStart, Point2D cdEnd) {
        NodePosition nodePosition = new NodePosition(abStart, abEnd, cdStart);
        NodePosition nodePosition2 = new NodePosition(abStart, abEnd, cdEnd);
        if (Math.signum(nodePosition.s) == Math.signum(nodePosition2.s)) {
            return false;
        }
        /*
         * A very important note: in [Kelly 4.3.3.4] it is said
         * that an intersection within the bounds of ab is only probable
         * when points of cd are on <i>opposing extensions</i> of ab;.
         * however, actually instead they must be <i>not on the same extension</i>.
         * The difference is that in my version (and in real cases) a line CD with C on an extension
         * and 0<D.r<1 should be tested for an intersection too.
         */
        return Range.contains(0, 1, nodePosition.r) && Range.contains(0, 1, nodePosition2.r)
                || !(nodePosition.r > 1 && nodePosition2.r > 1 || nodePosition.r < 0 && nodePosition2.r < 0);
    }

    /**
     * [Kelly figure 46]
     *
     * @param sourceNode
     * @param targetPoint
     * @param snapSize
     * @return
     */
    private Collection<Line2D> findSegmentsToTest(Point2D sourceNode, Point2D targetPoint, double snapSize) {
        // TODO: Optimize culling
        double minX = Math.min(sourceNode.x, targetPoint.x) - snapSize;
        double minY = Math.min(sourceNode.y, targetPoint.y) - snapSize;
        double maxX = Math.max(sourceNode.x, targetPoint.x) + snapSize;
        double maxY = Math.max(sourceNode.y, targetPoint.y) + snapSize;
        Geometry boundingBox = factory.createLineString(new Coordinate[]{
                new Coordinate(minX, minY),
                new Coordinate(maxX, maxY)
        }).getEnvelope();
        Collection<Line2D> answer = new LinkedList<>();
        for (Line2D road : roadCycle.edgeSet()) {

            LineString roadLine = factory.createLineString(new Coordinate[]{
                    new Coordinate(road.start.x, road.start.y),
                    new Coordinate(road.end.x, road.end.y)
            });
            if (roadLine.getEnvelope().intersects(boundingBox)) {
                answer.add(road);
            }
        }
        return answer;
    }
}
