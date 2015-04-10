package org.tendiwa.geometry.smartMesh;

import org.jgrapht.Graph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.Collection;
import java.util.Optional;

/**
 * [Kelly 4.3.3.5]
 * <p>
 * Handles the case when the unsnapped segment doesn't intersect any roads, but is snapped to {@code road} because
 * you can build a short enough perpendicular (projection) from {@link #targetNode} to {@code road}.
 */
final class SegmentSnapSearch implements EventSearch {
	private final Point2D sourceNode;
	private final Point2D targetNode;
	private final Graph<Point2D, Segment2D> fullNetworkGraph;
	private final Collection<Segment2D> segmentsToTest;
	private final double snapSize;
	private double minR;
	private SnapToSegment result;

	public SegmentSnapSearch(
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
		this.minR = 1;
	}

	@Override
	public Optional<PropagationEvent> find() {
		segmentsToTest.forEach(this::trySnapping);
		return Optional.ofNullable(result);
	}

	private void trySnapping(Segment2D road) {
		Optional<PointPosition> whereToSnap = segmentSnapPosition(road);
		if (!whereToSnap.isPresent()) {
			return;
		}
		Point2D targetPoint = whereToSnap.get().pointOnSegment(road);
		assert !targetPoint.equals(sourceNode);
		assert !fullNetworkGraph.containsVertex(targetPoint);
		result = new SnapToSegment(
			sourceNode,
			targetPoint,
			road
		);
	}

	private Optional<PointPosition> segmentSnapPosition(Segment2D segment) {
		if (segment.oneOfEndsIs(sourceNode)) {
			return Optional.empty();
		}
		PointPosition pointPosition = new PointPosition(
			segment.start,
			segment.end,
			targetNode
		);
		if (pointPosition.r >= minR) {
			return Optional.empty();
		}
		if (pointPosition.r < Vectors2D.EPSILON || pointPosition.r > 1 - Vectors2D.EPSILON) {
			return Optional.empty();
		}
		if (pointPosition.distance > snapSize - Vectors2D.EPSILON) {
			return Optional.empty();
		}
		return Optional.of(pointPosition);
	}
}
