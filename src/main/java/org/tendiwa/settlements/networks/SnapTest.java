package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.core.meta.Range;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.RayIntersection;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Checks if a 2d segment defined by a start and an end points snaps to any vertex or edge of a planar
 * graph.
 */
final class SnapTest {
	private final double snapSize;
	private final Point2D sourceNode;
	private Point2D targetNode;
	private final UndirectedGraph<Point2D, Segment2D> relevantRoadNetwork;
	private final HolderOfSplitCycleEdges holderOfSplitCycleEdges;
	private double minR;

	/**
	 * Checks if a 2d segment defined by a start and an end points snaps to any vertex or edge of a planar
	 * graph.
	 *
	 * @param snapSize
	 * 	Radius of snapping. If {@code targetNode} has any vertices or edges in this radius,
	 * 	it will be snapped to the closest of such vertices and edges. If this is less than {@link
	 * 	org.tendiwa.geometry.Vectors2D#EPSILON}, then it is set to that constant.
	 * @param sourceNode
	 * 	Start point of a 2d segment.
	 * @param targetNode
	 * 	End point of a 2d segment.
	 * @param relevantRoadNetwork
	 * 	A planar graph whose edges and vertices are tested for proximity to a 2d segment from {@code sourceNode} to
	 * 	{@code targetNode}.
	 * @param holderOfSplitCycleEdges
	 */
	SnapTest(
		double snapSize,
		Point2D sourceNode,
		Point2D targetNode,
		UndirectedGraph<Point2D, Segment2D> relevantRoadNetwork,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges
	) {
		this.snapSize = Math.max(snapSize, Vectors2D.EPSILON);
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.relevantRoadNetwork = relevantRoadNetwork;
		this.holderOfSplitCycleEdges = holderOfSplitCycleEdges;
		setTargetNode(targetNode);
		minR = 1 + snapSize / sourceNode.distanceTo(targetNode);
	}

	private void setTargetNode(Point2D node) {
		targetNode = node;
		minR = 1;
	}

	SnapEvent snap() {
		if (relevantRoadNetwork.containsVertex(targetNode)) {
			return new SnapEvent(targetNode, SnapEventType.NODE_SNAP, null);
		}
		Collection<Segment2D> roadsToTest = findSegmentsToTest(sourceNode, targetNode, snapSize);
		Point2D snapNode = null;
		Set<Point2D> verticesToTest = findEndpointsToTestForNodeSnap(roadsToTest);
		for (Point2D vertex : verticesToTest) {
//            if (isNeighborOfSourceNode(vertex) && minimalCycle
//                    .vertexList().contains(vertex)) {
//                continue;
//            }
			NodePosition nodePosition = new NodePosition(sourceNode, targetNode, vertex);
			if (isCloserSnapVertex(nodePosition)) {
				minR = nodePosition.r;
				snapNode = vertex;
			}
		}
		SnapEvent snapEvent = null;
		if (snapNode != null) {
			snapEvent = new SnapEvent(snapNode, SnapEventType.NODE_SNAP, null);
//			setTargetNode(snapNode);
		}
		for (Segment2D road : roadsToTest) {
			if (isRoadSticksToSegment(road)) {
				continue;
			}
			if (isSegmentIntersectionProbable(sourceNode, targetNode, road.start, road.end)) {
				RayIntersection intersection = new RayIntersection(
					sourceNode,
					targetNode,
					// TODO: Maybe there should be just a road, since it is a segment itself?
					new Segment2D(road.start, road.end)
				);
				if (intersection.r >= minR || intersection.r < 0) {
					continue;
				}
				if (intersection.intersects) {
					Point2D intersectionPoint = intersection.getLinesIntersectionPoint();
					boolean isIntersectionOnSourcePoint = intersectionPoint.equals(sourceNode);
//                    assert !(isIntersectionOnSourcePoint && snapSize > 0) : snapSize;
					if (isIntersectionOnSourcePoint) {
						return new SnapEvent(null, SnapEventType.NO_NODE, null);
					}
					if (snapEvent != null && intersectionPoint.equals(snapEvent.targetNode)) {
						continue;
					}
					assert !iDontRememberWhatItAsserts(road, intersectionPoint);
					assert !intersectionPoint.equals(road.end) : road.end.hashCode() + " it should have been a point snap";
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

		for (Segment2D road : roadsToTest) {
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
			if (Math.abs(nodePosition.distance - snapSize) > Vectors2D.EPSILON) {
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
	 * Among endpoints of {@code segments}, finds a set of vertices to test for a {@link org.tendiwa.settlements
	 * .SnapEventType#NODE_SNAP} event.
	 *
	 * @param segments
	 * @return
	 */
	private Set<Point2D> findEndpointsToTestForNodeSnap(Collection<Segment2D> segments) {
		Set<Point2D> answer = new HashSet<>();
		for (Segment2D segment : segments) {
			// Individual vertices will be added only once
			if (!segment.start.equals(sourceNode) && !segment.end.equals(sourceNode)) {
				assert !segment.start.equals(sourceNode);
				assert !segment.end.equals(sourceNode);
				answer.add(segment.start);
				answer.add(segment.end);
			}
		}
		return answer;
	}

	private boolean iDontRememberWhatItAsserts(Segment2D road, Point2D intersectionPoint) {
		// TODO: What the fuck it asserts?
		return Math.abs(road.start.distanceTo(road.end) - road.start.distanceTo(intersectionPoint) - road
			.end.distanceTo(intersectionPoint)) > 1;
	}

	/**
	 * Checks if one of road's vertices is {@link #sourceNode} or {@link #targetNode}.
	 *
	 * @param road
	 * 	A road.
	 * @return true if a road has {@link #sourceNode} or {@link #targetNode} as one of its ends, false otherwise.
	 */
	private boolean isRoadSticksToSegment(Segment2D road) {
		return road.start == sourceNode
			|| road.end == sourceNode
			|| road.start == targetNode
			|| road.end == targetNode;
	}

	/**
	 * Checks if a vertex in {@code nodePosition} is closer that the one that is currently found to be the closest.
	 * <p>
	 * If there was no previous found closest vertex, returns true.
	 *
	 * @param nodePosition
	 * 	A position of a vertex relative to a segment [sourceNode;targetNode].
	 * @return true if vertex defined by nodePosition is closer that the previous one, false otherwise.
	 */
	private boolean isCloserSnapVertex(NodePosition nodePosition) {
		return nodePosition.r < minR && nodePosition.r >= 0 && nodePosition.distance <= snapSize;
	}

	/**
	 * Checks if there is an edge between {@code vertex} and {@link #sourceNode}.
	 *
	 * @param vertex
	 * 	A vertex.
	 * @return true if there is an edge between a vertex and the source node, false otherwise.
	 */
	private boolean isNeighborOfSourceNode(Point2D vertex) {
		return relevantRoadNetwork.containsEdge(vertex, sourceNode);
	}

	/**
	 * [Kelly 4.3.3.4]
	 * <p>
	 * In [Kelly 4.3.3.4] there is no pseudocode for this function, it is described in the second paragraph.
	 * <p>
	 * Provides a quick heuristic to see if two lines should be tested for an intersection.
	 *
	 * @param abStart
	 * 	Start of line ab.
	 * @param abEnd
	 * 	End of line ab.
	 * @param cdStart
	 * 	Start of line cd.
	 * @param cdEnd
	 * 	End of line cd. Interchanging arguments for ab and cd should yield the same result.
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
	 * 	Source point of a segment <i>ab</i>.
	 * @param target
	 * 	Target point node of a segment <i>ab</i>.
	 * @param snapSize
	 * 	With of the grey area on the figure — how far away from the original segment do we search.
	 * @return A collection of all the segments that are close enough to the segment <i>ab</i>.
	 */
	private Collection<Segment2D> findSegmentsToTest(Point2D source, Point2D target, double snapSize) {
		double minX = Math.min(source.x, target.x) - snapSize;
		double minY = Math.min(source.y, target.y) - snapSize;
		double maxX = Math.max(source.x, target.x) + snapSize;
		double maxY = Math.max(source.y, target.y) + snapSize;
		Stream<Segment2D> roadStream = constructRoadsStream();
//		Stream<Segment2D> roadStream = relevantRoadNetwork.edgeSet().stream();
		return roadStream
			.filter(road -> {
				double roadMinX = Math.min(road.start.x, road.end.x);
				double roadMaxX = Math.max(road.start.x, road.end.x);
				double roadMinY = Math.min(road.start.y, road.end.y);
				double roadMaxY = Math.max(road.start.y, road.end.y);
				// http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
				return minX < roadMaxX && maxX > roadMinX && minY < roadMaxY && maxY > roadMinY;
			})
			.collect(Collectors.toList());
	}

	private Stream<Segment2D> constructRoadsStream() {
		Set<Set<Segment2D>> splitEdges = new LinkedHashSet<>();
		for (Segment2D edge : relevantRoadNetwork.edgeSet()) {
			if (holderOfSplitCycleEdges.isEdgeSplit(edge)) {
				splitEdges.add(holderOfSplitCycleEdges.getGraph(edge).edgeSet());
			}
		}
		Stream<Segment2D> originalRoadsStream = relevantRoadNetwork.edgeSet().stream()
			.filter(road -> !holderOfSplitCycleEdges.isEdgeSplit(road));
		Stream<Segment2D> splitRoadsStream = splitEdges.stream()
			.flatMap(a -> a.stream());
		return Stream.concat(originalRoadsStream, splitRoadsStream);
	}
}
