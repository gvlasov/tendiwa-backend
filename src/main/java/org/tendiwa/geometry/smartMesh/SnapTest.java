package org.tendiwa.geometry.smartMesh;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Checks if a 2d segment defined by a start and an end points snaps to any vertex or edge of a 2d graph.
 */
final class SnapTest {
	private final double snapSize;
	private final Point2D source;
	private final UndirectedGraph<Point2D, Segment2D> fullNetworkGraph;
	private final Point2D unsnappedTarget;
	/**
	 * Which roads can hold the point to snap to.
	 */
	private final Collection<Segment2D> segmentsToTest;
	private final Sector allowedSector;

	/**
	 * Checks if a 2d segment defined by a start and an end points snaps to any vertex or edge of a planar
	 * graph.
	 *
	 * @param snapSize
	 * 	Radius of snapping. If {@code target} has any vertices or edges in this radius,
	 * 	it will be snapped to the closest of such vertices and edges. If this is less than {@link
	 * 	org.tendiwa.geometry.Vectors2D#EPSILON}, then it is set to that constant.
	 * @param source
	 * 	Start point of the unsnapped segment.
	 * @param target
	 * 	End point of the unsnapped segment.
	 * @param fullNetworkGraph
	 * 	A planar graph whose edges and vertices are tested for proximity to a 2d segment from {@code source} to
	 * 	{@code target}.
	 */
	SnapTest(
		double snapSize,
		Point2D source,
		Point2D target,
		UndirectedGraph<Point2D, Segment2D> fullNetworkGraph,
		Sector allowedSector
	) {
		this.allowedSector = allowedSector;
		this.snapSize = Math.max(snapSize, Vectors2D.EPSILON);
		this.source = source;
		this.unsnappedTarget = target;
		this.fullNetworkGraph = fullNetworkGraph;
		this.segmentsToTest = findNearbySegments(source, target, snapSize);
	}

	/**
	 * Does all the node/road snapping computations and tells how the unsnapped segment should be
	 * snapped to something or even not snapped to anything at all.
	 *
	 * @return A description of how {@link #unsnappedTarget} snaps to a node, a road, or nothing.
	 */
	PropagationEvent snap() {
		if (canSnapRightAway()) {
			return new SnapToNode(source, unsnappedTarget);
		}
		PropagationEvent result = createUnsnappedResult();
		result = nodeSnapSearch().find().orElse(result);
		result = segmentIntersectionSearch(result).find().orElse(result);
		if (result instanceof NowhereToSnap) {
			result = segmentSnapSearch().find().orElse(result);
		}
		return result;
	}

	private boolean canSnapRightAway() {
		return fullNetworkGraph.containsVertex(unsnappedTarget);
	}

	private NowhereToSnap createUnsnappedResult() {
		return new NowhereToSnap(source, unsnappedTarget);
	}

	private EventSearch nodeSnapSearch() {
		return new NodeSnapSearch(
			source,
			unsnappedTarget,
			fullNetworkGraph,
			segmentsToTest,
			allowedSector,
			snapSize
		);
	}

	private EventSearch segmentSnapSearch() {
		return new SegmentSnapSearch(
			source,
			unsnappedTarget,
			fullNetworkGraph,
			segmentsToTest,
			snapSize
		);
	}

	private EventSearch segmentIntersectionSearch(PropagationEvent previousResult) {
		return new SegmentIntersectionSearch(
			source,
			previousResult.target(),
			segmentsToTest
		);
	}

	/**
	 * [Kelly figure 46]
	 * <p>
	 * Finds all segments that probably intersect with a segment <i>ab</i>.
	 *
	 * @param onePoint
	 * 	One endpoint of a segment <i>ab</i>.
	 * @param anotherPoint
	 * 	Another endpoint node of a segment <i>ab</i>.
	 * @param snapSize
	 * 	With of the grey area on the figure — how far away from the original segment do we search.
	 * @return A collection of all the segments that are close enough to the segment <i>ab</i>.
	 */
	private Collection<Segment2D> findNearbySegments(Point2D onePoint, Point2D anotherPoint, double snapSize) {
		RectangularHull hull = new BasicRectangularHull(
			Math.min(onePoint.x, anotherPoint.x) - snapSize,
			Math.max(onePoint.x, anotherPoint.x) + snapSize,
			Math.min(onePoint.y, anotherPoint.y) - snapSize,
			Math.max(onePoint.y, anotherPoint.y) + snapSize
		);
		return fullNetworkGraph.edgeSet().stream()
			.filter(hull::intersectsHull)
			.collect(Collectors.toList());
	}
}