package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.math.IntegerPermutationGenerator;

import java.awt.Color;
import java.util.*;

final class SecondaryRoadNetwork {

	/**
	 * [Kelly figure 42]
	 * <p>
	 */
	private final int roadsFromPoint;
	private final double roadSegmentLength;
	private final int maxNumOfStartPoints;
	private final double secondaryRoadNetworkRoadLengthDeviation;
	private final double secondaryRoadNetworkDeviationAngle;
	private final HolderOfSplitCycleEdges holderOfSplitCycleEdges;
	private final boolean favourAxisAlignedSegments;
	private final Set<Point2D> deadEnds = new HashSet<>();
	private final UndirectedGraph<Point2D, Segment2D> relevantNetwork;
	/**
	 * Nodes that form the enclosing cycle of this {@link NetworkWithinCycle}.
	 */
	private final Set<Point2D> cycleNodes;
	final SimpleGraph<Point2D, Segment2D> secRoadNetwork;
	private final CycleRing ring;
	private final double snapSize;
	private final double connectivity;
	private final Collection<Segment2D> filamentEdges;

	ImmutableSet<Point2D> filamentEndPoints;
	Set<DirectionFromPoint> filamentEnds;
	private final UndirectedGraph<Point2D, Segment2D> originalRoadGraph;
	private final Random random;
	/**
	 * A set of point including:
	 * <ol>
	 * <li>Vertices of the original graph;</li>
	 * <li>Vertices created on edges of the original graph;</li>
	 * <li>Vertices created on edges of the secondary road graph</li>
	 * </ol>
	 */
	private final ImmutableSet.Builder<Point2D> outerPointsBuilder = ImmutableSet.builder();
	final ImmutableSet<Point2D> exitsOnCycles;

	SecondaryRoadNetwork(
		int roadsFromPoint,
		int maxNumOfStartPoints,
		double roadSegmentLength,
		double snapSize,
		double connectivity,
		double secondaryRoadNetworkRoadLengthDeviation,
		double secondaryRoadNetworkDeviationAngle,
		boolean favourAxisAlignedSegments,
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		UndirectedGraph<Point2D, Segment2D> originalRoadGraph,
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges,
		Collection<Segment2D> filamentEdges,
		Random random
	) {
		this.snapSize = snapSize;
		this.connectivity = connectivity;
		this.maxNumOfStartPoints = maxNumOfStartPoints;
		this.secondaryRoadNetworkRoadLengthDeviation = secondaryRoadNetworkRoadLengthDeviation;
		this.secondaryRoadNetworkDeviationAngle = secondaryRoadNetworkDeviationAngle;
		this.holderOfSplitCycleEdges = holderOfSplitCycleEdges;
		this.filamentEdges = filamentEdges;
		this.originalRoadGraph = originalRoadGraph;
		this.random = random;
		this.roadsFromPoint = roadsFromPoint;
		this.roadSegmentLength = roadSegmentLength;
		this.favourAxisAlignedSegments = favourAxisAlignedSegments;
		this.relevantNetwork = relevantNetwork;

		this.cycleNodes = new HashSet<>(originalMinimalCycle.vertexList());
		updateRelevantNetworkWithNeighborCyclesSplitEdges();
		relevantNetwork.vertexSet().forEach(deadEnds::add);
		secRoadNetwork = new SimpleGraph<>(relevantNetwork.getEdgeFactory());
		ring = new CycleRing(originalMinimalCycle);
		buildSegment2DNetwork(originalMinimalCycle);
		exitsOnCycles = outerPointsBuilder.build();
	}

	private void updateRelevantNetworkWithNeighborCyclesSplitEdges() {
		Set<Segment2D> relevantNetworkEdges = ImmutableSet.copyOf(relevantNetwork.edgeSet());
		for (Segment2D edge : relevantNetworkEdges) {
			if (holderOfSplitCycleEdges.isEdgeSplit(edge)) {
				relevantNetwork.removeEdge(edge);
				UndirectedGraph<Point2D, Segment2D> splitEdgeGraph = holderOfSplitCycleEdges.getGraph(edge);
				splitEdgeGraph.vertexSet().forEach(relevantNetwork::addVertex);
				for (Segment2D splitEdge : splitEdgeGraph.edgeSet()) {
					relevantNetwork.addEdge(splitEdge.start, splitEdge.end, splitEdge);
				}


			}
		}

	}

	/**
	 * [Kelly figure 42]
	 * <p>
	 * Calculates initial road segments and processes road growth. Also finds out which nodes are secondary network's
	 * filament ends, if there are any.
	 *
	 * @param cycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 */
	private void buildSegment2DNetwork(MinimalCycle<Point2D, Segment2D> cycle) {
		Map<Segment2D, List<Point2D>> pointsOnPolygonBorder = startingPoints(cycle);
		List<Point2D> startingPoints = concatListsToOneList(pointsOnPolygonBorder);
//		TestCanvas.canvas.drawAll(startingPoints, DrawingPoint2D.withColorAndSize(Color.red, 5));

		int numberOfStartingPoints = startingPoints.size();
		int[] randomPointIndices = IntegerPermutationGenerator.generateUsingFisherYates(
			numberOfStartingPoints,
			numberOfStartingPoints,
			random
		);
		int lastRandomPointIndexIndex = 0;
		Set<DirectionFromPoint> filamentEnds = new HashSet<>();
		Map<Point2D, Segment2D> pointsToOriginalRoads = mapPointsToOriginalRoads(
			pointsOnPolygonBorder,
			numberOfStartingPoints
		);

		// How many starting points have a deviated perpendicular edge of secondary road
		// network coming out of them.
		int startingPointsUsed = 0;

		while (lastRandomPointIndexIndex < numberOfStartingPoints) {
			Deque<DirectionFromPoint> nodeQueue = new ArrayDeque<>();
//			Collection<Segment2D> startingRoadsSnappedTo = new HashSet<>(startingPoints.size());

			do {
				Point2D sourceNode = startingPoints.get(randomPointIndices[lastRandomPointIndexIndex]);
//			if (startingRoadsSnappedTo.contains(road)) {
//				continue;
//			}

				Segment2D road = getSplitRoadWherePointIs(sourceNode, pointsToOriginalRoads.get(sourceNode));
				// Made dead end so two new roads are not inserted to the network.
				deadEnds.add(sourceNode);
				insertNode(road, sourceNode);
				// Made not-dead end so a road can be placed from it.
				deadEnds.remove(sourceNode);
				Segment2D originalEdge = holderOfSplitCycleEdges.findOriginalEdge(road);
				double direction = deviatedBoundaryPerpendicular(originalEdge);

				SnapEvent snapEvent = tryPlacingRoad(sourceNode, direction, true);
				if (doesNotSnapToDeadEnd(snapEvent)) {
					nodeQueue.push(new DirectionFromPoint(snapEvent.targetNode, direction));
					outerPointsBuilder.add(sourceNode);
				}
				if (
					snapEvent != null
						&& snapEvent.eventType == SnapEventType.ROAD_SNAP
					) {
//					startingRoadsSnappedTo.add(snapEvent.road);
					startingPointsUsed--;
				}
				deadEnds.add(sourceNode);
				startingPointsUsed++;
				lastRandomPointIndexIndex++;
//			assert !nodeQueue.isEmpty();
				while (!nodeQueue.isEmpty()) {
					spanSecondaryNetworkFromStartingSegmentsEnds(filamentEnds, nodeQueue);
				}
			} while (startingPointsUsed <= maxNumOfStartPoints);
		}
		this.filamentEnds = removeMultidegreeFilamentEnds(filamentEnds);
		filamentEndPoints = nodes2TheirPoints(filamentEnds);
	}

	private void spanSecondaryNetworkFromStartingSegmentsEnds(Set<DirectionFromPoint> filamentEnds, Deque<DirectionFromPoint> nodeQueue) {
		DirectionFromPoint node = nodeQueue.removeLast();
		boolean addedAnySegments = false;
		for (int i = 1; i < roadsFromPoint; i++) {
			double newDirection = deviateDirection(node.direction + Math.PI + i * (Math.PI * 2 / roadsFromPoint));
			SnapEvent snapEvent = tryPlacingRoad(node.node, newDirection, false);
			if (snapEvent == null) {
				continue;
			}
			Point2D newNode = snapEvent.targetNode;
			if (newNode != null && !isDeadEnd(newNode)) {
				nodeQueue.push(new DirectionFromPoint(newNode, newDirection));
				addedAnySegments = true;
			}
		}
		if (!addedAnySegments) {
			// This does not guarantee that only degree 1 nodes will be added to filament ends,
			// but it culls most of wrong edges.
			filamentEnds.add(node);
		}
	}

	/**
	 * [Kelly figure 42]
	 * <p>
	 * Finds the roads to start secondary road network generation from.
	 *
	 * @param cycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @return Several roads.
	 */
	private Map<Segment2D, List<Point2D>> startingPoints(MinimalCycle<Point2D, Segment2D> cycle) {
		return IntervalsAlongPolygonBorder.compute(
			cycle.vertexList(),
			roadSegmentLength,
			secondaryRoadNetworkRoadLengthDeviation,
			originalRoadGraph::getEdge,
			random
		);
//		List<Segment2D> edges = Lists.newArrayList(cycle);
//		Collections.sort(
//			edges,
//			(o1, o2) -> (int) Math.signum(o2.start.distanceTo(o2.end) - o1.start.distanceTo(o1.end))
//		);
//		int numberOfStartPoints = Math.min(maxNumOfStartPoints, originalMinimalCycle.vertexList().size());
//		int numberOfStartPoints = cycle.size();
//		return edges.subList(0, numberOfStartPoints);
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
	private SnapEvent tryPlacingRoad(Point2D source, double direction, boolean isStartingRoad) {
		assert !isDeadEnd(source);
		double roadLength = deviatedLength(roadSegmentLength);
		double dx = roadLength * Math.cos(direction);
		double dy = roadLength * Math.sin(direction);
		Point2D unsnappedTargetNode = new Point2D(source.x + dx, source.y + dy);
		SnapEvent snapEvent = new SnapTest(snapSize, source, unsnappedTargetNode, relevantNetwork, holderOfSplitCycleEdges).snap();
		assert !source.equals(snapEvent.targetNode);
		switch (snapEvent.eventType) {
			case NO_SNAP:
				assert unsnappedTargetNode == snapEvent.targetNode;
				relevantNetwork.addVertex(unsnappedTargetNode);
				addRoadToSecondaryNetwork(source, unsnappedTargetNode);
				return snapEvent;
			case ROAD_SNAP:
				if (random.nextDouble() < connectivity) {
					if (isDeadEnd(snapEvent.road.start) && isDeadEnd(snapEvent.road.end)) {
						if (isStartingRoad) {
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
				if (random.nextDouble() < connectivity) {
					if (isStartingRoad) {
//						TestCanvas.canvas.draw(new Segment2D(source, snapEvent.targetNode), DrawingSegment2D
//							.withColorDirected(Color.cyan));
						return null;
					}
					if (!relevantNetwork.containsVertex(snapEvent.targetNode)) {
						relevantNetwork.addVertex(snapEvent.targetNode);
					}
					addRoadToSecondaryNetwork(source, snapEvent.targetNode);
					return null;
				} else {
					return null;
				}
			case NO_NODE:
				return null;
			default:
				throw new RuntimeException();
		}
	}

	/**
	 * Returns a slightly changed direction.
	 * <p>
	 * If {@link #favourAxisAlignedSegments} is true, then the answer will be tilted towards the closest
	 * {@code Math.PI/2*n} angle.
	 *
	 * @param newDirection
	 * 	Original angle in radians.
	 * @return Slightly changed angle in radians. Answer is not constrained to [0; 2*PI] interval — it may be any
	 * number.
	 */
	private double deviateDirection(double newDirection) {
		double v = random.nextDouble();
		if (favourAxisAlignedSegments) {
			double closestAxisParallelDirection = Math.round(newDirection / (Math.PI / 2)) * (Math.PI / 2);
			if (Math.abs(closestAxisParallelDirection - newDirection) < secondaryRoadNetworkDeviationAngle) {
				return closestAxisParallelDirection;
			} else {
				return newDirection + secondaryRoadNetworkDeviationAngle * Math.signum(closestAxisParallelDirection - newDirection);
			}
		} else {
			return newDirection - secondaryRoadNetworkDeviationAngle + v * secondaryRoadNetworkDeviationAngle * 2;
		}
	}

	private double deviatedLength(double roadSegmentLength) {
		return roadSegmentLength - secondaryRoadNetworkRoadLengthDeviation / 2 + random.nextDouble() *
			secondaryRoadNetworkRoadLengthDeviation;
	}

	/**
	 * [Kelly figure 42]
	 *
	 * @param edge
	 * 	An edge of {@link RoadsPlanarGraphModel#originalRoadGraph}.
	 * @return An angle in radians perpendicular to {@code edge}. The angle is slightly deviated.
	 */
	private double deviatedBoundaryPerpendicular(Segment2D edge) {
		assert originalRoadGraph.containsEdge(edge);
		double angle = edge.start.angleTo(edge.end);
		return deviateDirection(angle + Math.PI / 2 * ring.getDirection(edge));
	}

	private List<Point2D> concatListsToOneList(Map<Segment2D, List<Point2D>> startingPoints) {
		int numberOfPoints = startingPoints.values().stream()
			.map(Collection::size)
			.reduce(0, (a, b) -> a + b);
		List<Point2D> answer = new ArrayList<>(numberOfPoints);
		startingPoints.values().forEach(answer::addAll);
		return answer;
	}

	private boolean doesNotSnapToDeadEnd(SnapEvent snapEvent) {
		return snapEvent != null && snapEvent.targetNode != null && !isDeadEnd(snapEvent.targetNode);
	}

	/**
	 * Creates a new map from points on segments to those segments.
	 *
	 * @param pointsOnPolygonBorder
	 * @param numberOfAllPoints
	 * @return
	 */
	private Map<Point2D, Segment2D> mapPointsToOriginalRoads(
		Map<Segment2D, List<Point2D>> pointsOnPolygonBorder,
		int numberOfAllPoints
	) {
		Map<Point2D, Segment2D> answer = new HashMap<>(numberOfAllPoints);
		for (Segment2D originalRoad : pointsOnPolygonBorder.keySet()) {
			assert originalRoadGraph.containsEdge(originalRoad);
			for (Point2D point : pointsOnPolygonBorder.get(originalRoad)) {
				answer.put(point, originalRoad);
			}
		}
		return answer;
	}

	/**
	 * Finds on which split part of {@code originalRoad} (or if it is on {@code originalRoad} itself) resides a
	 * {@code point}.
	 *
	 * @param point
	 * 	A point on {@code originalRoad}.
	 * @param originalRoad
	 * 	An edge of {@code originalRoadGraph}.
	 * @return A sub-segment of {@code originalRoad}.
	 */
	private Segment2D getSplitRoadWherePointIs(Point2D point, Segment2D originalRoad) {
		assert originalRoadGraph.containsEdge(originalRoad);
		boolean dx = originalRoad.dx() != 0;
		if (holderOfSplitCycleEdges.isEdgeSplit(originalRoad)) {
			UndirectedGraph<Point2D, Segment2D> graphOfAnOriginalRoad = holderOfSplitCycleEdges.getGraph(originalRoad);
			for (Segment2D splitEdgePart : graphOfAnOriginalRoad.edgeSet()) {
				if (isPointInBoundingRectangle(point, splitEdgePart, dx)) {
					// It is enough to just check that a bounding rectangle contains the point because all edges in
					// this graph lie on the same line.
					return splitEdgePart;
				}
			}
		} else {
			assert isPointInBoundingRectangle(point, originalRoad, dx);
			return originalRoad;
		}
		throw new RuntimeException("Split road not found");
	}

	private boolean isPointInBoundingRectangle(Point2D point, Segment2D segment, boolean dx) {
		if (dx) {
			double minX = Math.min(segment.start.x, segment.end.x);
			double maxX = Math.max(segment.start.x, segment.end.x);
			return point.x > minX && point.x < maxX;
		} else {
			double minY = Math.min(segment.start.y, segment.end.y);
			double maxY = Math.max(segment.start.y, segment.end.y);
			return point.y > minY && point.y < maxY;
		}
	}

	/**
	 * For a set of point-directions, creates a set of just points without their directions.
	 *
	 * @param filamentEnds
	 * 	A set of nodes.
	 * @return A set of points of point-directions.
	 */
	private ImmutableSet<Point2D> nodes2TheirPoints(Set<DirectionFromPoint> filamentEnds) {
		ImmutableSet.Builder<Point2D> builder = ImmutableSet.builder();
		for (DirectionFromPoint node : filamentEnds) {
			builder.add(node.node);
		}
		return builder.build();
	}

	/**
	 * Removes from a set all nodes that don't have a degree of 1.
	 *
	 * @param nodes
	 * 	An initial set of nodes.
	 * @return The same modified set of all nodes from the initial set that have a degree of 1.
	 */
	private Set<DirectionFromPoint> removeMultidegreeFilamentEnds(Set<DirectionFromPoint> nodes) {
		Iterator<DirectionFromPoint> iterator = nodes.iterator();
		if (nodes.isEmpty()) {
			return nodes;
		}
		DirectionFromPoint point;
		do {
			point = iterator.next();
			if (relevantNetwork.degreeOf(point.node) != 1) {
				iterator.remove();
			}
		} while (iterator.hasNext());
		return nodes;
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
		assert relevantNetwork.containsVertex(source);
		assert relevantNetwork.containsVertex(target);
		relevantNetwork.addEdge(source, target);
		assert !ShamosHoeyAlgorithm.areIntersected(relevantNetwork.edgeSet());
		if (isOriginalRoadBeingSplit(source, target)) {
			// This happens when a node is inserted into a road, for both new roads.
			return false;
		}
		return true;
	}

	private void addRoadToSecondaryNetwork(Point2D source, Point2D target) {
		addRoad(source, target);
		secRoadNetwork.addVertex(source);
		secRoadNetwork.addVertex(target);
		secRoadNetwork.addEdge(source, target);
		if (cycleNodes.contains(target)) {
			// outerPointsBuilder may contain the target point, but then it just won't be added.
			outerPointsBuilder.add(target);
		}
	}

	private boolean isOriginalRoadBeingSplit(Point2D source, Point2D target) {
		return isDeadEnd(source) && isDeadEnd(target);
	}

	private boolean isDeadEnd(Point2D node) {
		return deadEnds.contains(node);
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
	private void insertNode(Segment2D road, Point2D point) {
		if (road.end.equals(point)) {
			return;
		}
		assert relevantNetwork.containsEdge(road);
		boolean isCycleEdge = isCycleEdge(road);
		minimumDistanceAssert(road, point);
		relevantNetwork.removeEdge(road);
		relevantNetwork.addVertex(point);
		if (isCycleEdge) {
			holderOfSplitCycleEdges.splitEdge(road, point);
			addRoad(road.start, point);
			addRoad(point, road.end);
		} else {
			secRoadNetwork.removeEdge(road);
			secRoadNetwork.addVertex(point);
			addRoadToSecondaryNetwork(road.start, point);
			addRoadToSecondaryNetwork(point, road.end);
		}
		if (cycleNodes.contains(road.start) && cycleNodes.contains(road.end)) {
			cycleNodes.add(point);
			outerPointsBuilder.add(point);
		}
	}

	private boolean isCycleEdge(Segment2D road) {
		return originalRoadGraph.containsEdge(road) || holderOfSplitCycleEdges.isEdgeSplit(road);
	}

	private void minimumDistanceAssert(Segment2D road, Point2D point) {
		assert !road.start.equals(point) : "point is start";
		assert !road.end.equals(point) : "point is end";
		assert road.start.distanceTo(point) > Vectors2D.EPSILON
			: road.start.distanceTo(point) + " " + road.start.distanceTo(road.end);
		assert road.end.distanceTo(point) > Vectors2D.EPSILON
			: road.end.distanceTo(point) + " " + road.start.distanceTo(road.end);
	}
}
