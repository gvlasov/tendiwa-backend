package org.tendiwa.geometry.smartMesh;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.core.meta.Range;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.RayIntersection;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Checks if a 2d segment defined by a start and an end points snaps to any vertex or edge of a 2d graph.
 */
final class SnapTest {
	private final double snapSize;
	/**
	 * Start of the unsnapped segment.
	 */
	private final Point2D sourceNode;
	private final UndirectedGraph<Point2D, Segment2D> fullNetworkGraph;
	private Point2D targetNode;
	private double minR;

	/**
	 * End of the unsnapped segment.
	 */

	private SnapEvent result;
	/**
	 * Which roads can hold the point to snap to.
	 */
	private final Collection<Segment2D> roadsToTest;

	/**
	 * Checks if a 2d segment defined by a start and an end points snaps to any vertex or edge of a planar
	 * graph.
	 *
	 * @param snapSize
	 * 	Radius of snapping. If {@code targetNode} has any vertices or edges in this radius,
	 * 	it will be snapped to the closest of such vertices and edges. If this is less than {@link
	 * 	org.tendiwa.geometry.Vectors2D#EPSILON}, then it is set to that constant.
	 * @param sourceNode
	 * 	Start point of the unsnapped segment.
	 * @param targetNode
	 * 	End point of the unsnapped segment.
	 * @param fullNetworkGraph
	 * 	A planar graph whose edges and vertices are tested for proximity to a 2d segment from {@code sourceNode} to
	 * 	{@code targetNode}.
	 */
	SnapTest(
		double snapSize,
		Point2D sourceNode,
		Point2D targetNode,
		UndirectedGraph<Point2D, Segment2D> fullNetworkGraph
	) {
		this.snapSize = Math.max(snapSize, Vectors2D.EPSILON);
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.fullNetworkGraph = fullNetworkGraph;
		this.minR = 1 + snapSize / sourceNode.distanceTo(targetNode);
		this.roadsToTest = findNearbySegments(sourceNode, targetNode, snapSize);
	}

	/**
	 * Does all the node/road snapping computations are done here and tells how the unsnapped segment should be
	 * snapped to something or even not snapped to anything at all.
	 *
	 * @return A description of how {@link #targetNode} snaps to a node, a road, or nothing.
	 */
	SnapEvent snap() {
		if (fullNetworkGraph.containsVertex(targetNode)) {
			return new SnapEventNode(sourceNode, targetNode);
		}
		// Next lines try various snappings and set #result to the best possible snap event.
		snapToNothing();
		findSnapNode();
		if (result instanceof SnapEventNode) {
			targetNode = result.target();
			minR = 1;
		}
		roadsToTest.forEach(this::tryIntersectingRoad);
		roadsToTest.forEach(this::trySnappingToRoad);
		return result;
	}

	private void snapToNothing() {
		result = new NowhereToSnap(sourceNode, targetNode);
	}

	/**
	 * [Kelly 4.3.3.5]
	 * <p>
	 * Handles the case when the unsnapped segment doesn't intersect any roads, but is snapped to {@code road} because
	 * you can build a short enough perpendicular (projection) from {@link #targetNode} to {@code road}.
	 *
	 * @param road
	 * 	A road to project {@link #targetNode} to.
	 */
	private void trySnappingToRoad(Segment2D road) {
		Optional<PointPosition> whereToSnap = segmentSnapPosition(road);
		if (!whereToSnap.isPresent()) {
			return;
		}
		Point2D targetPoint = placePointOnLine(road, whereToSnap.get());
		assert !targetPoint.equals(sourceNode);
		assert !fullNetworkGraph.containsVertex(targetPoint);
		result = new SnapEventRoad(
			sourceNode,
			targetPoint,
			road
		);
	}

	private Point2D placePointOnLine(Segment2D segment, PointPosition whereToSnap) {
		return new Point2D(
			segment.start.x + whereToSnap.r * (segment.end.x - segment.start.x),
			segment.start.y + whereToSnap.r * (segment.end.y - segment.start.y)
		);
	}

	private Optional<PointPosition> segmentSnapPosition(Segment2D segment) {
		if (segment.start == sourceNode || segment.end == sourceNode) {
			return Optional.empty();
		}
		if (result instanceof NowhereToSnap) {
			return Optional.empty();
		}
		PointPosition pointPosition = new PointPosition(
			segment.start,
			segment.end,
			targetNode
		);
		if (pointPosition.r < Vectors2D.EPSILON || pointPosition.r > 1 - Vectors2D.EPSILON) {
			return Optional.empty();
		}
		if (pointPosition.distance > snapSize - Vectors2D.EPSILON) {
			return Optional.empty();
		}
		return Optional.of(pointPosition);
	}

	/**
	 * Tries finding the road intersecting the unsnapped segment as close to segment's source as possible, and sets
	 * {@link #result} if it is found.
	 */
	private void tryIntersectingRoad(Segment2D road) {
		if (roadSticksToCurrentResult(road)) {
			return;
		}
		if (isSegmentIntersectionProbable(sourceNode, targetNode, road.start, road.end)) {
			RayIntersection intersection = new RayIntersection(sourceNode, targetNode, road);
			if (isIntersectionInsideUnsnappedSegment(intersection)) {
				Point2D intersectionPoint = intersection.getLinesIntersectionPoint();
				assert !intersectionPoint.equals(sourceNode) : "Commented code below should be used";
//					if (intersectionPoint.equals(sourceNode)) {
//						return new SnapEvent(null, SnapEventType.NO_NODE, null);
//					}
				if (result != null && intersectionPoint.equals(result.target())) {
					return;
				}
				assert !iDontRememberWhatItAsserts(road, intersectionPoint);
				assert !intersectionPoint.equals(road.end) : road.end.hashCode() + " it should have been a point snap";
				result = new SnapEventRoad(sourceNode, intersectionPoint, road);
				minR = intersection.r;
			}
		}
	}

	/**
	 * Sets {@link #result} to snap to the best (closest) node, or to be unsnapped (snap to nothing) if there is no
	 * appropriate node to snap to.
	 *
	 * @return The node to snap to.
	 */
	private void findSnapNode() {
		Set<Point2D> verticesToTest = findEndpointsToTestForNodeSnap(roadsToTest);
		for (Point2D vertex : verticesToTest) {
			PointPosition pointPosition = new PointPosition(sourceNode, targetNode, vertex);
			if (isVertexBetterThanCurrentBestVertex(vertex, pointPosition)) {
				minR = pointPosition.r;
				result = new SnapEventNode(sourceNode, vertex);
			}
		}
	}

	private boolean isVertexBetterThanCurrentBestVertex(Point2D vertex, PointPosition pointPosition) {
		return isCloserSnapVertex(pointPosition) && connectingVertexIntroducesNoIntersectingSegments(vertex, roadsToTest);
	}

	private boolean connectingVertexIntroducesNoIntersectingSegments(
		Point2D vertex,
		Collection<Segment2D> roadsToTest
	) {
		if (fullNetworkGraph.containsEdge(sourceNode, vertex)) {
			Segment2D segment = new Segment2D(sourceNode, vertex);
			if (roadsToTest.stream().anyMatch(road -> road.intersects(segment))) {
				return false;
			}
		}
		return true;
	}

	private boolean isIntersectionInsideUnsnappedSegment(RayIntersection intersection) {
		return intersection.r < minR && intersection.r >= 0 && intersection.intersects;
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
	private boolean roadSticksToCurrentResult(Segment2D road) {
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
	 * @param pointPosition
	 * 	A position of a vertex relative to a segment [sourceNode;targetNode].
	 * @return true if vertex defined by nodePosition is closer that the previous one, false otherwise.
	 */
	private boolean isCloserSnapVertex(PointPosition pointPosition) {
		return pointPosition.r < minR && pointPosition.r >= 0 && pointPosition.distance <= snapSize;
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
	private boolean isSegmentIntersectionProbable(
		Point2D abStart,
		Point2D abEnd,
		Point2D cdStart,
		Point2D cdEnd
	) {
		PointPosition pointPosition = new PointPosition(abStart, abEnd, cdStart);
		PointPosition pointPosition2 = new PointPosition(abStart, abEnd, cdEnd);
		if (Math.signum(pointPosition.s) == Math.signum(pointPosition2.s)) {
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
		return Range.contains(0, 1, pointPosition.r) && Range.contains(0, 1, pointPosition2.r)
			|| !(pointPosition.r > 1 && pointPosition2.r > 1 || pointPosition.r < 0 && pointPosition2.r < 0);
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
	private Collection<Segment2D> findNearbySegments(Point2D source, Point2D target, double snapSize) {
		double minX = Math.min(source.x, target.x) - snapSize;
		double minY = Math.min(source.y, target.y) - snapSize;
		double maxX = Math.max(source.x, target.x) + snapSize;
		double maxY = Math.max(source.y, target.y) + snapSize;
		Stream<Segment2D> roadStream = fullNetworkGraph.edgeSet().stream();
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
}
