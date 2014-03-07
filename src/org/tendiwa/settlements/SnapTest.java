package org.tendiwa.settlements;

import com.vividsolutions.jts.geom.*;
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
private final Point2D sourceNode;
private final Point2D targetPoint;
private final SimpleGraph<Point2D, Line2D> roadCycle;
private double minR = Double.MAX_VALUE;

SnapTest(double snapSize, Point2D sourceNode, Point2D targetPoint, SimpleGraph<Point2D, Line2D> roadCycle) {
	this.snapSize = snapSize;
	this.sourceNode = sourceNode;
	this.targetPoint = targetPoint;
	this.roadCycle = roadCycle;
}

SnapEvent snap() {
	Collection<Line2D> segmentsToTest = findSegmentsToTest(sourceNode, targetPoint, snapSize);

	Point2D snapPoint = null;
	Set<Point2D> verticesToTest = new HashSet<>();
	for (Line2D segment : segmentsToTest) {
		// Individual vertices will be added only once
		if (segment.start != sourceNode) {
			verticesToTest.add(segment.start);
		}
		if (segment.end != sourceNode) {
			verticesToTest.add(segment.end);
		}
	}
	for (Point2D vertex : verticesToTest) {
		NodePosition nodePosition = nodeProximityTest(sourceNode, targetPoint, vertex);
		if (nodePosition != null && nodePosition.distance <= snapSize) {
			snapPoint = vertex;
		}
	}
	if (snapPoint != null) {
		System.out.println(1);
		return new SnapEvent(snapPoint, SnapEventType.NODE_SNAP, null);
	}
	for (Line2D segment : segmentsToTest) {
		if (segment.start == sourceNode || segment.end == sourceNode) {
			continue;
		}
		if (segmentIntersectionTest(sourceNode, targetPoint, segment.start, segment.end)) {
			Coordinate intersection = new LineSegment(
				new Coordinate(sourceNode.x, sourceNode.y),
				new Coordinate(targetPoint.x, targetPoint.y)
			).intersection(
				new LineSegment(
					new Coordinate(segment.start.x, segment.start.y),
					new Coordinate(segment.end.x, segment.end.y)
				)
			);
			assert intersection != null : "There must be an intersection";
			System.out.println(2);
			System.out.println(segment.start);
			System.out.println(segment.end);
			System.out.println(sourceNode);
			System.out.println(targetPoint);
			return new SnapEvent(
				new Point2D(intersection.x, intersection.y),
				SnapEventType.ROAD_SNAP,
				segment
			);
		}
	}
	for (Line2D segment : segmentsToTest) {
		if (segment.end.distanceTo(targetPoint) < snapSize) {
			if (segment.start == sourceNode || segment.end == sourceNode) {
				continue;
			}
			NodePosition nodePosition = new NodePosition(segment.start, segment.end, targetPoint);
			System.out.println(3);
			System.out.println(nodePosition.r);
			System.out.println(segment.start);
			System.out.println(segment.end);
			System.out.println(sourceNode);
			System.out.println(targetPoint);
			return new SnapEvent(
				new Point2D(
					segment.start.x + nodePosition.r * (segment.end.x - segment.start.x),
					segment.start.y + nodePosition.r * (segment.end.y - segment.start.y)
				),
				SnapEventType.ROAD_SNAP,
				segment
			);
		}
	}
	System.out.println(0);
	return new SnapEvent(targetPoint, SnapEventType.NO_SNAP, null);
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
private boolean segmentIntersectionTest(Point2D abStart, Point2D abEnd, Point2D cdStart, Point2D cdEnd) {
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
private NodePosition nodeProximityTest(Point2D a, Point2D b, Point2D p) {
	NodePosition nodePosition = new NodePosition(a, b, p);
	if (nodePosition.r >= minR) {
		return null;
	}
	minR = nodePosition.r;
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
private Collection<Line2D> findSegmentsToTest(Point2D sourceNode, Point2D targetPoint, double snapSize) {
	// TODO: Optimize culling
	Geometry boundingBox = factory.createLineString(new Coordinate[]{
		new Coordinate(sourceNode.x - snapSize, sourceNode.y - snapSize),
		new Coordinate(targetPoint.x + snapSize, targetPoint.y + snapSize)
	}).getEnvelope();
	Collection<Line2D> answer = new LinkedList<>();
	for (Line2D edge : roadCycle.edgeSet()) {
		LineString edgeLine = factory.createLineString(new Coordinate[]{
			new Coordinate(edge.start.x, edge.start.y),
			new Coordinate(edge.end.x, edge.end.y)
		});
		if (edgeLine.getEnvelope().intersects(boundingBox)) {
			answer.add(edge);
		}
	}
	return answer;
}
}
