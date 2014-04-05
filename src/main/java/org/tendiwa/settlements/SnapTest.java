package org.tendiwa.settlements;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.meta.Range;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SnapTest {
    private final double snapSize;
    private final Point2D sourceNode;
    private Point2D targetNode;
    private final SimpleGraph<Point2D, Line2D> relevantRoadNetwork;
    private double minR;

    SnapTest(
            double snapSize,
            Point2D sourceNode,
            Point2D targetNode,
            SimpleGraph<Point2D, Line2D> relevantRoadNetwork) {
        this.snapSize = snapSize;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.relevantRoadNetwork = relevantRoadNetwork;
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
                    assert !intersectionPoint.equals(sourceNode);
                    if (Math.abs(road.start.distanceTo(road.end) - road.start.distanceTo(intersectionPoint) - road.end.distanceTo(intersectionPoint)) > 1) {
                        assert false;
                    }
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
            if (nodePosition.r < 0 || nodePosition.r > 1) {
                continue;
            }
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
     * <p>
     * Finds all segments that probably intersect with a segment <i>ab</i>.
     *
     * @param source
     *         Source point of a segment <i>ab</i>.
     * @param target
     *         Target point node of a segment <i>ab</i>.
     * @param snapSize
     *         With of the grey area on the figure — how far away from the original segment do we search.
     * @return A collection of all the segments that are close enough to the segment <i>ab</i>.
     */
    private Collection<Line2D> findSegmentsToTest(Point2D source, Point2D target, double snapSize) {
        double minX = Math.min(source.x, target.x) - snapSize;
        double minY = Math.min(source.y, target.y) - snapSize;
        double maxX = Math.max(source.x, target.x) + snapSize;
        double maxY = Math.max(source.y, target.y) + snapSize;
        return relevantRoadNetwork.edgeSet().stream()
                .filter(road -> {
                    double roadMinX = Math.min(road.start.x, road.end.x);
                    double roadMaxX = Math.max(road.start.x, road.end.x);
                    double roadMinY = Math.min(road.start.y, road.end.y);
                    double roadMaxY = Math.max(road.start.y, road.end.y);
                    // http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other                    minX < roadMaxX && maxX > roadMinX &&
                    return minX < roadMaxX && maxX > roadMinX && minY < roadMaxY && maxY > roadMinY;
                })
                .collect(Collectors.toList());
    }
}
