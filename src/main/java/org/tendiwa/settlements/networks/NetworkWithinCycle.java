package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.MinimalCycle;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

/**
 * [Kelly section 4.3.1]
 * <p>
 * A part of a city bounded by a fundamental basis cycle (one of those in <i>minimal cycle basis</i> from [Kelly
 * section
 * 4.3.1, figure 41].
 */
public final class NetworkWithinCycle {
	private final UndirectedGraph<Point2D, Segment2D> relevantNetwork;
	/**
	 * Nodes that form the enclosing cycle of this {@link NetworkWithinCycle}.
	 */
	final Set<Point2D> cycleNodes;
	private final NetworkToBlocks blockDivision;
	private final boolean favourAxisAlignedSegments;
	private final HolderOfSplitCycleEdges holderOfSplitCycleEdges;
	private final SimpleGraph<Point2D, Segment2D> secRoadNetwork;
	private MinimalCycle<Point2D, Segment2D> originalMinimalCycle;
	private final UndirectedGraph<Point2D, Segment2D> lowLevelRoadGraph;
	private Collection<Segment2D> filamentEdges;

	private ImmutableSet<Point2D> filamentEndPoints;
	/**
	 * [Kelly figure 42]
	 * <p>
	 */
	private final int roadsFromPoint;
	private final double roadSegmentLength;
	/**
	 * Coordinates of cycle's vertices sorted in a clockwise or counter-clockwise order.
	 */
	private final Coordinate[] ring;
	/**
	 * Order of sorting of {@link #ring}.
	 */
	private final boolean isCycleClockwise;
	private final double snapSize;
	private final double connectivity;
	private final Random random;
	/**
	 * A set of point including:
	 * <ol>
	 * <li>Vertices of the original graph;</li>
	 * <li>Vertices created on edges of the original graph;</li>
	 * <li>Vertices created on edges of the secondary road graph</li>
	 * </ol>
	 */
	private final Set<Point2D> deadEnds = new HashSet<>();
	private final ImmutableSet.Builder<Point2D> outerPointsBuilder = ImmutableSet.builder();
	private final ImmutableSet<Point2D> exitsOnCycles;
	private final int maxNumOfStartPoints;
	private final double secondaryRoadNetworkRoadLengthDeviation;
	private final double secondaryRoadNetworkDeviationAngle;
	public double v;
	private Set<DirectionFromPoint> filamentEnds;
	/**
	 * Lazily initialized graph of edges that form the cycle this network is enclosed in.
	 */
	private UndirectedGraph<Point2D, Segment2D> cycleGraph;

	/**
	 * Returns a set points where secondary road network is connected with the cycle.
	 *
	 * @return A set points where secondary road network is connected with the cycle.
	 */
	public ImmutableSet<Point2D> exitsOnCycles() {
		return exitsOnCycles;
	}

	/**
	 * @param graph
	 * 	A preconstructed graph of low level roads, constructed by
	 * 	{@link org.tendiwa.settlements.networks.RoadsPlanarGraphModel#constructNetworkOriginalGraph(org.tendiwa.graphs.MinimalCycle,
	 *    java.util.Set, java.util.Collection)}
	 * @param originalMinimalCycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @param lowLevelRoadGraph
	 * 	Graph that is bounding all cells.
	 * @param filamentEdges
	 * 	A collection of all the edges of a {@link RoadsPlanarGraphModel#originalRoadGraph} that are not
	 * 	part of any minimal cycles. The same collection is passed to all the CityCells.
	 * @param roadsFromPoint
	 * 	[Kelly figure 42, variable ParamDegree]
	 * 	<p>
	 * 	How many lines would normally go from one point of secondary road network. A NetworkWithinCycle is not
	 * 	guaranteed
	 * 	to have exactly {@code maxRoadsFromPoint} starting roads, because such amount might not fit into a cell.
	 * @param roadSegmentLength
	 * 	[Kelly figure 42, variable ParamSegmentLength]
	 * 	<p>
	 * 	Mean length of secondary network roads.
	 * @param snapSize
	 * 	[Kelly figure 42, variable ParamSnapSize]
	 * 	<p>
	 * 	A radius around secondary roads' end points inside which new end points would snap to existing ones.
	 * @param connectivity
	 * 	[Kelly figure 42, variable ParamConnectivity]
	 * 	<p>
	 * 	How likely it is to snap to node or road when possible. When connectivity == 1.0, algorithm will always
	 * 	snap when possible. When connectivity == 0.0, algorithm will never snap.
	 * @param secondaryRoadNetworkDeviationAngle
	 * 	An angle in radians. How much is secondary roads' direction randomized.
	 * 	<p>
	 * 	Kelly doesn't have this as a parameter, it is implied in [Kelly figure 42] under "deviate newDirection"
	 * 	and "calculate deviated boundaryRoad perpendicular".
	 * @param secondaryRoadNetworkRoadLengthDeviation
	 * 	A length in cells. How much is secondary roads' length randomized.
	 * 	<p>
	 * 	Kelly doesn't have this as a parameter, it is implied in [Kelly figure 42] under "calculate deviated
	 * 	ParamSegmentLength".
	 * @param maxNumOfStartPoints
	 * 	Number of starting points for road generation
	 * 	<p>
	 * 	In [Kelly figure 43] there are 2 starting points.
	 * 	<p>
	 * 	A NetworkWithinCycle is not guaranteed to have exactly {@code maxRoadsFromPoint} starting roads, because such
	 * 	amount might not fit into a cell.
	 * @param random
	 * 	A seeded {@link java.util.Random} used to generate the parent {@link RoadsPlanarGraphModel}.
	 * @param favourAxisAlignedSegments
	 * @param holderOfSplitCycleEdges
	 */

	NetworkWithinCycle(
		UndirectedGraph<Point2D, Segment2D> graph,
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		UndirectedGraph<Point2D, Segment2D> lowLevelRoadGraph,
		Collection<Segment2D> filamentEdges,
		int roadsFromPoint,
		double roadSegmentLength,
		double snapSize,
		double connectivity,
		double secondaryRoadNetworkDeviationAngle,
		double secondaryRoadNetworkRoadLengthDeviation,
		int maxNumOfStartPoints,
		Random random,
		boolean favourAxisAlignedSegments,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges
	) {
		this.originalMinimalCycle = originalMinimalCycle;
		this.lowLevelRoadGraph = lowLevelRoadGraph;
		this.filamentEdges = filamentEdges;
		this.roadsFromPoint = roadsFromPoint;
		this.roadSegmentLength = roadSegmentLength;
		this.snapSize = snapSize;
		this.connectivity = connectivity;
		this.secondaryRoadNetworkDeviationAngle = secondaryRoadNetworkDeviationAngle;
		this.secondaryRoadNetworkRoadLengthDeviation = secondaryRoadNetworkRoadLengthDeviation;
		this.random = random;
		this.maxNumOfStartPoints = maxNumOfStartPoints;

		relevantNetwork = graph;
		this.favourAxisAlignedSegments = favourAxisAlignedSegments;
		this.holderOfSplitCycleEdges = holderOfSplitCycleEdges;
		secRoadNetwork = new SimpleGraph<>(graph.getEdgeFactory());


		relevantNetwork.vertexSet().forEach(deadEnds::add);


		ring = buildRing(originalMinimalCycle);
		isCycleClockwise = false;
		cycleNodes = new HashSet<>(originalMinimalCycle.vertexList());

		buildSegment2DNetwork(originalMinimalCycle);

		exitsOnCycles = outerPointsBuilder.build();

		blockDivision = new NetworkToBlocks(
			relevantNetwork,
			filamentEnds,
			roadSegmentLength + secondaryRoadNetworkRoadLengthDeviation,
			holderOfSplitCycleEdges
		);
	}

	private Coordinate[] buildRing(MinimalCycle<Point2D, Segment2D> originalMinimalCycle) {
		Coordinate[] coordinates = pointListToCoordinateArray(originalMinimalCycle.vertexList());
		// TODO: Are all cycles counter-clockwise? (because of the MCB algorithm)
		Coordinate[] ring;
		if (!CGAlgorithms.isCCW(coordinates)) {
			List<Coordinate> list = Arrays.asList(coordinates);
			Collections.reverse(list);
			ring = list.toArray(new Coordinate[list.size()]);
		} else {
			ring = coordinates;
		}
		return ring;
	}

	/**
	 * Transforms a list of {@link org.tendiwa.geometry.Point2D}s to an array of {@link
	 * com.vividsolutions.jts.geom.Coordinate}s.
	 *
	 * @param points
	 * 	A list of points.
	 * @return An array of coordinates.
	 */
	private Coordinate[] pointListToCoordinateArray(List<Point2D> points) {
		List<Coordinate> collect = points.stream()
			.map(a -> new Coordinate(a.x, a.y))
			.collect(Collectors.toList());
		collect.add(new Coordinate(points.get(0).x, points.get(0).y));
		return collect.toArray(new Coordinate[points.size()]);
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
		Deque<DirectionFromPoint> nodeQueue = new ArrayDeque<>();
		Collection<Segment2D> startingRoads = startingRoads(cycle);
		Collection<Segment2D> startingRoadsSnappedTo = new HashSet<>(startingRoads.size());
		for (Segment2D road : startingRoads) {
			if (startingRoadsSnappedTo.contains(road)) {
				continue;
			}
			Point2D sourceNode = calculateDeviatedMidPoint(road);
			// Made dead end so two new roads are not inserted to network.
			deadEnds.add(sourceNode);
			insertNode(road, sourceNode);
			// Made not-dead end so a road can be placed from it.
			deadEnds.remove(sourceNode);
			double direction = deviatedBoundaryPerpendicular(road);

			SnapEvent snapEvent = tryPlacingRoad(sourceNode, direction, true);
			if (snapEvent != null && snapEvent.targetNode != null && !isDeadEnd(snapEvent.targetNode)) {
				nodeQueue.push(new DirectionFromPoint(snapEvent.targetNode, direction));
				outerPointsBuilder.add(sourceNode);
			}
			if (
				snapEvent != null
					&& snapEvent.eventType == SnapEventType.ROAD_SNAP
					&& startingRoads.contains(snapEvent.road)) {
				startingRoadsSnappedTo.add(snapEvent.road);
			}
			deadEnds.add(sourceNode);
		}
		Set<DirectionFromPoint> filamentEnds = new HashSet<>();
//		assert !nodeQueue.isEmpty();
		while (!nodeQueue.isEmpty()) {
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
		this.filamentEnds = removeMultidegreeFilamentEnds(filamentEnds);
		filamentEndPoints = nodes2TheirPoints(filamentEnds);
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

	private boolean isDeadEnd(Point2D node) {
		return deadEnds.contains(node);
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
		v = random.nextDouble();
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
		// TODO: Actually deviate the angle
		double angle = edge.start.angleTo(edge.end);
		return deviateDirection(angle + Math.PI / 2
			* (isCycleClockwise ? -1 : 1)
			* (isStartBeforeEndInRing(edge) ? 1 : -1));
	}

	/**
	 * Checks if {@link org.tendiwa.geometry.Segment2D#start} of an edge appears earlier in a {@link #ring} than
	 * {@link org.tendiwa.geometry.Segment2D#end}.
	 *
	 * @return true if {@code edge.start} appears earlier than {@code edge.end}, false otherwise.
	 */
	private boolean isStartBeforeEndInRing(Segment2D edge) {
		Coordinate start = new Coordinate(edge.start.x, edge.start.y);
		Coordinate end = new Coordinate(edge.end.x, edge.end.y);
		assert start != end;
		for (int i = 0; i < ring.length; i++) {
			if (ring[i].equals(start)) {
				if (ring[i + 1].equals(end)) {
					return true;
				} else {
					assert ring[i == 0 ? ring.length - 2 : i - 1].equals(end);
					return false;
				}
			}
		}
		throw new RuntimeException(start + " is not before or after " + end);
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

	/**
	 * Creates an unmodifiable view of {@link #secRoadNetwork}.
	 *
	 * @return An unmodifiable graph containing this NetworkWithinCycle's secondary road network.
	 */
	public UndirectedGraph<Point2D, Segment2D> network() {
		return new UnmodifiableUndirectedGraph<>(secRoadNetwork);
	}

	public UndirectedGraph<Point2D, Segment2D> cycle() {
		if (cycleGraph == null) {
			initCycleGraph();
		}
		return cycleGraph;
	}

	/**
	 * Constructs the enclosing cycle of this network from {@link #originalMinimalCycle} and {@link
	 * #holderOfSplitCycleEdges}.
	 */
	private void initCycleGraph() {
		assert cycleGraph == null;
		cycleGraph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
		for (Segment2D edge : originalMinimalCycle) {
			cycleGraph.addVertex(edge.start);
			cycleGraph.addVertex(edge.end);
			if (holderOfSplitCycleEdges.isEdgeSplit(edge)) {
				UndirectedGraph<Point2D, Segment2D> splitGraph = holderOfSplitCycleEdges.getGraph(edge);
				splitGraph.vertexSet().forEach(cycleGraph::addVertex);
				for (Segment2D splitEdge : splitGraph.edgeSet()) {
					cycleGraph.addEdge(splitEdge.start, splitEdge.end, splitEdge);
				}
			} else {
				cycleGraph.addEdge(edge.start, edge.end, edge);
			}
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
	private void insertNode(Segment2D road, Point2D point) {
		if (road.end.equals(point)) {
			return;
		}
		assert relevantNetwork.containsEdge(road);
		boolean isCycleEdge = isCycleEdge(road);
		assertMinimumDistance(road, point);
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
		return lowLevelRoadGraph.containsEdge(road) || holderOfSplitCycleEdges.isEdgeSplit(road);
	}

	private void assertMinimumDistance(Segment2D road, Point2D point) {
		assert !road.start.equals(point) : "point is start";
		assert !road.end.equals(point) : "point is end";
		assert road.start.distanceTo(point) > Vectors2D.EPSILON
			: road.start.distanceTo(point) + " " + road.start.distanceTo(road.end);
		assert road.end.distanceTo(point) > Vectors2D.EPSILON
			: road.end.distanceTo(point) + " " + road.start.distanceTo(road.end);
	}

	private Point2D calculateDeviatedMidPoint(Segment2D road) {
		return new Point2D(
			road.start.x + (road.end.x - road.start.x) / 2,
			road.start.y + (road.end.y - road.start.y) / 2
		);
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
	private Collection<Segment2D> startingRoads(MinimalCycle<Point2D, Segment2D> cycle) {
		List<Segment2D> edges = Lists.newArrayList(cycle);
		Collections.sort(
			edges,
			(o1, o2) -> (int) Math.signum(o2.start.distanceTo(o2.end) - o1.start.distanceTo(o1.end))
		);
//		int numberOfStartPoints = Math.min(maxNumOfStartPoints, originalMinimalCycle.vertexList().size());
		int numberOfStartPoints = cycle.size();
		return edges.subList(0, numberOfStartPoints);
	}

	public Set<SecondaryRoadNetworkBlock> getEnclosedBlocks() {
		return blockDivision.getEnclosedBlocks();
	}

	public ImmutableSet<Point2D> filamentEnds() {
		return filamentEndPoints;
	}

}
