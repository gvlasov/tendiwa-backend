package org.tendiwa.geometry.smartMesh;

import org.jgrapht.Graph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

final class NodeSnapSearch implements EventSearch {
	private final Point2D sourceNode;
	private final Point2D targetNode;
	private final Graph<Point2D, Segment2D> fullNetworkGraph;
	private final Collection<Segment2D> segmentsToTest;
	private final double snapSize;
	private double minR;

	NodeSnapSearch(
		Point2D sourceNode,
		Point2D targetNode,
		Graph<Point2D, Segment2D> fullNetworkGraph,
		Collection<Segment2D> segmentsToTest,
		double snapSize
	) {
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.fullNetworkGraph = fullNetworkGraph;
		this.segmentsToTest = segmentsToTest;
		this.snapSize = snapSize;
		this.minR = 1 + snapSize / sourceNode.distanceTo(targetNode);
	}

	private boolean isNeighbor(Point2D vertex) {
		return fullNetworkGraph.containsEdge(sourceNode, vertex);
	}

	/**
	 * Finds the best (closest) node, or returns empty if there is no appropriate node to snap to.
	 *
	 * @return The node to snap to, or empty.
	 */
	@Override
	public Optional<SnapEvent> find() {
		Set<Point2D> verticesToTest = findEndpointsToTestForNodeSnap(segmentsToTest);
		SnapEvent result = null;
		for (Point2D vertex : verticesToTest) {
			PointPosition pointPosition = new PointPosition(sourceNode, targetNode, vertex);
			if (isVertexBetterThanCurrentBestVertex(vertex, pointPosition)) {
				minR = pointPosition.r;
				result = new SnapToNode(sourceNode, vertex);
			}
		}
		return Optional.ofNullable(result).filter(r->!isNeighbor(r.target()));
	}

	private boolean isVertexBetterThanCurrentBestVertex(Point2D vertex, PointPosition pointPosition) {
		return isCloserSnapVertex(pointPosition)
			&& connectingVertexIntroducesNoIntersectingSegments(vertex, segmentsToTest);
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

	private boolean connectingVertexIntroducesNoIntersectingSegments(
		Point2D vertex,
		Collection<Segment2D> roadsToTest
	) {
		if (fullNetworkGraph.containsEdge(sourceNode, vertex)) {
			// TODO: Use an edge of the graph
			Segment2D segment = sourceNode.segmentTo(vertex);
			if (roadsToTest.stream().anyMatch(road -> road.intersects(segment))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Among endpoints of {@code segments}, finds a set of vertices to test for a {@link org.tendiwa.settlements
	 * .SnapEventType#NODE_SNAP} event.
	 */
	private Set<Point2D> findEndpointsToTestForNodeSnap(Collection<Segment2D> segments) {
		Set<Point2D> answer = new HashSet<>();
		for (Segment2D segment : segments) {
			// Individual vertices will be added only once
			if (!segment.start.equals(sourceNode) && !segment.end.equals(sourceNode)) {
				assert !segment.isOneOfEnds(sourceNode);
				answer.add(segment.start);
				answer.add(segment.end);
			}
		}
		return answer;
	}
}
