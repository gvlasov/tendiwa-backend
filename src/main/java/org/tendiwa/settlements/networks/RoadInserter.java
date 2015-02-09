package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.geometry.extensions.straightSkeleton.Bisector;

import java.awt.Color;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

/**
 * Inserts new roads into {@link org.tendiwa.settlements.networks.SecondaryRoadNetwork}.
 */
public class RoadInserter {
	private final UndirectedGraph<Point2D, Segment2D> relevantNetwork;
	final SimpleGraph<Point2D, Segment2D> secRoadNetwork;
	private final UndirectedGraph<Point2D, Segment2D> cycleGraph;
	private final ImmutableSet.Builder<Point2D> outerPointsBuilder;
	private final Set<Point2D> deadEnds;
	private final HolderOfSplitCycleEdges holderOfSplitCycleEdges;
	private final Collection<Segment2D> filamentEdges;
	private final NetworkGenerationParameters networkGenerationParameters;
	private final Random random;

	public RoadInserter(
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		UndirectedGraph<Point2D, Segment2D> cycleGraph,
		ImmutableSet.Builder<Point2D> outerPointsBuilder,
		Set<Point2D> deadEnds,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges,
		Collection<Segment2D> filamentEdges,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {
		this.relevantNetwork = relevantNetwork;
		this.cycleGraph = cycleGraph;
		this.outerPointsBuilder = outerPointsBuilder;
		this.deadEnds = deadEnds;
		this.holderOfSplitCycleEdges = holderOfSplitCycleEdges;
		this.filamentEdges = filamentEdges;
		this.networkGenerationParameters = networkGenerationParameters;
		this.random = random;

		this.secRoadNetwork = new SimpleGraph<>(relevantNetwork.getEdgeFactory());
		relevantNetwork.vertexSet().forEach(deadEnds::add);
	}

	/**
	 * Adds a new edge between two vertices that may or may not exist in NetworkWithinCycle's graph.
	 *
	 * @param source
	 * 	One vertex.
	 * @param target
	 * 	Another vertex (order is irrelevant since graphs are undirected in NetworkWithinCycle).
	 */
	private boolean addRoad(Point2D source, Point2D target) {
		assert relevantNetwork.containsVertex(source)
			&& relevantNetwork.containsVertex(target);
		relevantNetwork.addEdge(source, target);
		assert !ShamosHoeyAlgorithm.areIntersected(relevantNetwork.edgeSet());
		TestCanvas.canvas.draw(
			new Segment2D(source, target),
			DrawingSegment2D.withColorThin(Color.blue)
		);
		return !isOriginalRoadBeingSplit(source, target);
	}

	private void addRoadToSecondaryNetwork(Point2D source, Point2D target) {
		addRoad(source, target);
		secRoadNetwork.addVertex(source);
		secRoadNetwork.addVertex(target);
		secRoadNetwork.addEdge(source, target);
		if (cycleGraph.containsVertex(target)) {
			// outerPointsBuilder may contain the target point, but then it just won't be added.
			outerPointsBuilder.add(target);
		}
	}

	/**
	 * [Kelly figure 42]
	 * <p>
	 * Adds new node between two existing nodes, removing an existing road between them and placing 2 new roads to
	 * road network.
	 *
	 * @param road
	 * 	A road from {@link #relevantNetwork} on which a node is being inserted.
	 * @param point
	 * 	A node on that road where the node resides.
	 */
	void insertNode(Segment2D road, Point2D point) {
		if (road.end.equals(point)) {
			throw new RuntimeException("Inserting a point at the endpoint of exsting edge");
			// return; // This used to work
		}
		assert relevantNetwork.containsEdge(road);
		minimumDistanceAssert(road, point);
		relevantNetwork.removeEdge(road);
		relevantNetwork.addVertex(point);
		if (cycleGraph.containsEdge(road)) {
			insertNodeToPrimaryNetwork(road, point);
		} else {
			insertNodeToSecondaryNetwork(road, point);
		}
		if (cycleGraph.containsEdge(road)) {
			cycleGraph.addVertex(point);
			cycleGraph.removeEdge(road);
			cycleGraph.addEdge(road.start, point, relevantNetwork.getEdge(road.start, point));
			cycleGraph.addEdge(point, road.end, relevantNetwork.getEdge(point, road.end));
			outerPointsBuilder.add(point);
		}
	}

	private void insertNodeToSecondaryNetwork(Segment2D road, Point2D point) {
		secRoadNetwork.removeEdge(road);
		addRoadToSecondaryNetwork(road.start, point);
		addRoadToSecondaryNetwork(point, road.end);
	}

	private void insertNodeToPrimaryNetwork(Segment2D road, Point2D point) {
		holderOfSplitCycleEdges.splitEdge(road, point);
		addRoad(road.start, point);
		addRoad(point, road.end);
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
			relevantNetwork,
			holderOfSplitCycleEdges
		).snap();
		assert !source.equals(snapEvent.targetNode);
		switch (snapEvent.eventType) {
			case NO_SNAP:
				assert unsnappedTargetNode == snapEvent.targetNode;
				relevantNetwork.addVertex(unsnappedTargetNode);
				addRoadToSecondaryNetwork(source, unsnappedTargetNode);
				return snapEvent;
			case ROAD_SNAP:
				if (random.nextDouble() < networkGenerationParameters.connectivity) {
					if (isDeadEnd(snapEvent.road.start) && isDeadEnd(snapEvent.road.end)) {
						if (prohibitSnappingRightAway) {
							return null;
						}
						deadEnds.add(snapEvent.targetNode);
					}
					if (holderOfSplitCycleEdges.isEdgeSplit(snapEvent.road)) {
						relevantNetwork.addVertex(snapEvent.road.start);
						relevantNetwork.addVertex(snapEvent.road.end);
					}
					insertNode(snapEvent.road, snapEvent.targetNode);
					addRoadToSecondaryNetwork(source, snapEvent.targetNode);
					if (!filamentEdges.contains(snapEvent.road)) {
						deadEnds.add(snapEvent.targetNode);
					}
					return snapEvent;
				} else {
					return null;
				}
			case NODE_SNAP:
				if (random.nextDouble() < networkGenerationParameters.connectivity) {
					if (prohibitSnappingRightAway) {
						TestCanvas.canvas.draw(new Segment2D(source, snapEvent.targetNode), DrawingSegment2D
							.withColorThin(Color.green));
						return null;
					}
					if (!relevantNetwork.containsVertex(snapEvent.targetNode)) {
						relevantNetwork.addVertex(snapEvent.targetNode);
					}
					addRoadToSecondaryNetwork(source, snapEvent.targetNode);
					return snapEvent;
				} else {
					return null;
				}
			case NO_NODE:
				return null;
			default:
				throw new RuntimeException();
		}
	}

	void tryPlacingRoadFromEnclosedCycle(Point2D a, Point2D b, Point2D c, boolean toLeft) {
		Bisector bisector = new Bisector(
			new Segment2D(a, b),
			new Segment2D(b, c),
			b,
			!toLeft
		);
		Segment2D segment = bisector.asSegment(deviatedLength(networkGenerationParameters.roadSegmentLength));
		assert deadEnds.contains(segment.start);
		deadEnds.remove(segment.start);
		tryPlacingRoad(segment.start, segment.start.angleTo(segment.end), false);
		deadEnds.add(segment.start);
	}

	private void minimumDistanceAssert(Segment2D road, Point2D point) {
		assert !road.start.equals(point) : "point is start";
		assert !road.end.equals(point) : "point is end";
		assert road.start.distanceTo(point) > Vectors2D.EPSILON
			: road.start.distanceTo(point) + " " + road.start.distanceTo(road.end);
		assert road.end.distanceTo(point) > Vectors2D.EPSILON
			: road.end.distanceTo(point) + " " + road.start.distanceTo(road.end);
	}

	private boolean isOriginalRoadBeingSplit(Point2D source, Point2D target) {
		return isDeadEnd(source) && isDeadEnd(target);
	}

	boolean isDeadEnd(Point2D node) {
		return deadEnds.contains(node);
	}

	private double deviatedLength(double roadSegmentLength) {
		return roadSegmentLength - networkGenerationParameters.secondaryRoadNetworkRoadLengthDeviation / 2 + random.nextDouble() *
			networkGenerationParameters.secondaryRoadNetworkRoadLengthDeviation;
	}

	void insertStartingPoint(Segment2D actualEdge, Point2D startingPoint) {
		insertNode(actualEdge, startingPoint);
		deadEnds.add(startingPoint);
	}
}
