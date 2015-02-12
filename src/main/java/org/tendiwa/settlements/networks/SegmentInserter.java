package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.geometry.extensions.straightSkeleton.Bisector;
import org.tendiwa.graphs.CommonEdgeSplitter;
import org.tendiwa.graphs.CommonEdgeSplitter.SplitEdgesPair;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.*;

/**
 * Inserts new roads into {@link org.tendiwa.settlements.networks.NetworksProducer#fullGraph} and its subgraphs.
 */
public class SegmentInserter {
	private final UndirectedGraph<Point2D, Segment2D> fullGraph;
	private final OrientedCycle enclosingCycle;
	final UndirectedGraph<Point2D, Segment2D> secondaryNetworkGraph;
	private final Collection<OrientedCycle> enclosedCycles;
	private final CommonEdgeSplitter<Point2D, Segment2D> commonEdgeSplitter;
	private final NetworkGenerationParameters networkGenerationParameters;
	private final Random random;

	public SegmentInserter(
		UndirectedGraph<Point2D, Segment2D> fullGraph,
		UndirectedGraph<Point2D, Segment2D> secondaryNetworkGraph,
		OrientedCycle enclosingCycle,
		Collection<OrientedCycle> enclosedCycles,
		CommonEdgeSplitter<Point2D, Segment2D> commonEdgeSplitter,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {
		this.fullGraph = fullGraph;
		this.enclosingCycle = enclosingCycle;
		this.enclosedCycles = enclosedCycles;
		this.commonEdgeSplitter = commonEdgeSplitter;
		this.networkGenerationParameters = networkGenerationParameters;
		this.random = random;
		this.secondaryNetworkGraph = secondaryNetworkGraph;
	}


	/**
	 * [Kelly figure 42, function placeSegment]
	 * <p>
	 * Tries adding a new road to the secondary road network graph.
	 *
	 * @param source
	 * 	Start node of a new road.
	 * @param direction
	 * 	Angle of a road to x-axis.
	 * @return The new node, or null if placing did not succeed.
	 */
	SnapEvent tryPlacingRoad(Point2D source, double direction, boolean prohibitSnappingRightAway) {
		assert !isDeadEnd(source);
		double roadLength = deviatedLength(networkGenerationParameters.roadSegmentLength);
		double dx = roadLength * Math.cos(direction);
		double dy = roadLength * Math.sin(direction);
		Point2D unsnappedTargetNode = new Point2D(source.x + dx, source.y + dy);

		SnapEvent snapEvent = new SnapTest(
			networkGenerationParameters.snapSize,
			source,
			unsnappedTargetNode,
			fullGraph
		).snap();

		assert !source.equals(snapEvent.targetNode);
		if (snapEvent.eventType == SnapEventType.NO_SNAP) {
			assert snapEvent.targetNode == unsnappedTargetNode;
			fullGraph.addVertex(snapEvent.targetNode);
			addSecondaryNetworkEdge(source, snapEvent.targetNode);
			return snapEvent;
		} else if (!prohibitSnappingRightAway) {
			if (snapEvent.eventType == SnapEventType.NODE_SNAP) {
				if (chanceToConnect()) {
					assert fullGraph.containsVertex(snapEvent.targetNode);
					addSecondaryNetworkEdge(source, snapEvent.targetNode);
					return snapEvent;
				}
			} else if (snapEvent.eventType == SnapEventType.ROAD_SNAP) {
				if (chanceToConnect()) {
					assert fullGraph.containsVertex(snapEvent.road.start);
					assert fullGraph.containsVertex(snapEvent.road.end);
					splitEdge(snapEvent.road, snapEvent.targetNode);
					addSecondaryNetworkEdge(source, snapEvent.targetNode);
					return snapEvent;
				}
			} else {
				throw new RuntimeException("Wrong event");
			}
		} else {
			TestCanvas.canvas.draw(new Segment2D(source, snapEvent.targetNode), DrawingSegment2D
				.withColorThin(Color.green));
		}
		return null;
	}

	/**
	 * Creates an edge between two vertices and adds that edge to relevant graphs:
	 * <ul>
	 * <li>{@link #fullGraph}</li>
	 * <li>{@link #secondaryNetworkGraph}</li>
	 * </ul>
	 *
	 * @param source
	 * 	Start of segment.
	 * @param target
	 * 	End of segment.
	 */
	private void addSecondaryNetworkEdge(Point2D source, Point2D target) {
		Segment2D edge = new Segment2D(source, target);
		fullGraph.addEdge(source, target, edge);
		assert !ShamosHoeyAlgorithm.areIntersected(fullGraph.edgeSet());
		TestCanvas.canvas.draw(
			edge,
			DrawingSegment2D.withColorThin(Color.blue)
		);
		secondaryNetworkGraph.addEdge(source, target, edge);
		commonEdgeSplitter.addEdgeForGraph(fullGraph, edge);
	}

	/**
	 * [Kelly figure 42]
	 * <p>
	 * Adds new node between two existing nodes, removing an existing segment between them and placing 2 new roads to
	 * segment network.
	 * <p>
	 * Edges are split in the following graphs:
	 * <ul>
	 * <li>{@link #fullGraph}</li>
	 * <li>{@link org.tendiwa.settlements.networks.NetworksProducer#splitOriginalGraph}</li>
	 * <li>{@link #enclosingCycle} or one of {@link #enclosedCycles}</li>
	 * </ul>
	 *
	 * @param segment
	 * 	A segment from {@link #fullGraph} on which a node is being inserted.
	 * @param point
	 * 	A node on that segment where the node resides.
	 * @return Result of splitting an edge into two edges. Segments in result are identical to those inserted into
	 * graphs.
	 */
	SplitEdgesPair<Segment2D> splitEdge(Segment2D segment, Point2D point) {
		assert !segment.end.equals(point) && !segment.start.equals(point);
		assert fullGraph.containsEdge(segment);
		minimumDistanceAssert(segment, point);

		boolean updateEnclosingCycle = enclosingCycle.graph().containsEdge(segment);
		Optional<OrientedCycle> relevantEnclosedCycle = null;
		if (!updateEnclosingCycle) {
			relevantEnclosedCycle = enclosedCycles.stream()
				.filter(c -> c.graph().containsEdge(segment))
				.findAny();
		}

		SplitEdgesPair<Segment2D> splitEdgesPair = commonEdgeSplitter.splitEdge(segment, segment.start, segment.end, point);

		if (updateEnclosingCycle) {
			enclosingCycle.updateDirectionInformation(segment, point);
		} else if (relevantEnclosedCycle.isPresent()) {
			relevantEnclosedCycle.get().updateDirectionInformation(segment, point);
		}

		return splitEdgesPair;
	}

	private boolean chanceToConnect() {
		return random.nextDouble() < networkGenerationParameters.connectivity;
	}

	void tryPlacingRoadFromEnclosedCycle(Point2D a, Point2D b, Point2D c, boolean toLeft) {
		Bisector bisector = new Bisector(
			new Segment2D(a, b),
			new Segment2D(b, c),
			b,
			!toLeft
		);
		Segment2D segment = bisector.asSegment(networkGenerationParameters.roadSegmentLength);
		tryPlacingRoad(segment.start, segment.start.angleTo(segment.end), false);
	}

	private void minimumDistanceAssert(Segment2D road, Point2D point) {
		assert !road.start.equals(point) : "point is start";
		assert !road.end.equals(point) : "point is end";
		assert road.start.distanceTo(point) > Vectors2D.EPSILON
			: road.start.distanceTo(point) + " " + road.start.distanceTo(road.end);
		assert road.end.distanceTo(point) > Vectors2D.EPSILON
			: road.end.distanceTo(point) + " " + road.start.distanceTo(road.end);
	}

	boolean isDeadEnd(Point2D node) {
		return enclosingCycle.graph().containsVertex(node);
	}

	private double deviatedLength(double roadSegmentLength) {
		return roadSegmentLength - networkGenerationParameters.secondaryNetworkSegmentLengthDeviation / 2 + random.nextDouble() *
			networkGenerationParameters.secondaryNetworkSegmentLengthDeviation;
	}

	Set<Point2D> snapAndInsertStartingPoints(Map<Segment2D, List<Point2D>> pointsOnPolygonBorder) {
		int numberOfPoints = (int) pointsOnPolygonBorder.values().stream()
			.flatMap(Collection::stream)
			.count();

		Set<Point2D> points = new LinkedHashSet<>(numberOfPoints);
		Set<Segment2D> edgesHoldingStartingPoints = pointsOnPolygonBorder.keySet();
		for (Segment2D edge : edgesHoldingStartingPoints) {
			assert fullGraph.containsEdge(edge);
			SplitSegment splitSegment = new SplitSegment(pointsOnPolygonBorder.get(edge).size() + 1);
			for (Point2D startingPoint : pointsOnPolygonBorder.get(edge)) {
				Point2D segmentEndToSnap = findSegmentEndToSnap(startingPoint, edge);
				if (segmentEndToSnap == null) {
					points.add(startingPoint);
					Segment2D actualEdge = splitSegment.getSplitPartWithPoint(startingPoint);
					SplitEdgesPair<Segment2D> splitEdgesPair = splitEdge(actualEdge, startingPoint);
					splitSegment.split(
						edge,
						splitEdgesPair.oneEdge,
						splitEdgesPair.anotherEdge
					);
				} else {
					points.add(segmentEndToSnap); // Set automatically controls multiple addition.
				}
			}
		}
		return points;
	}

	/**
	 * If {@code startingPoint} can be snapped to one or both ends of {@code edge}, returns the closest of ends.
	 * Otherwise returns null.
	 *
	 * @param startingPoint
	 * 	A point to snap.
	 * @param edge
	 * 	An edge to whose ends to snap.
	 * @return Closest snappable end of {@code edge} or null.
	 */
	@Nullable
	private Point2D findSegmentEndToSnap(Point2D startingPoint, Segment2D edge) {
		double toStart = startingPoint.squaredDistanceTo(edge.start);
		double toEnd = startingPoint.squaredDistanceTo(edge.end);
		// TODO: snapSize should be squared here?
		if (toStart < toEnd) {
			if (toStart < networkGenerationParameters.snapSize) {
				return edge.start;
			}
		} else {
			if (toEnd < networkGenerationParameters.snapSize) {
				return edge.end;
			}
		}
		return null;
	}
}
