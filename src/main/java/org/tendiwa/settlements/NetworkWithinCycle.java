package org.tendiwa.settlements;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;

import java.util.*;
import java.util.stream.Collectors;

/**
 * [Kelly section 4.3.1]
 * <p>
 * A part of a city bounded by a fundamental basis cycle (one of those in <i>minimal cycle basis</i> from [Kelly
 * section
 * 4.3.1, figure 41].
 */
public class NetworkWithinCycle {
	private final SimpleGraph<Point2D, Segment2D> relevantNetwork;
	private final Set<Point2D> cycleNodes;
	private final DivisionOfSpaceInsideCycleIntoBlocks blockDivision;
	private TestCanvas canvas;
	private final SimpleGraph<Point2D, Segment2D> secRoadNetwork;
	private MinimalCycle<Point2D, Segment2D> minimalCycle;
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
	private double secondaryRoadNetworkDeviationAngle;
	private final Random random;
	private Set<Point2D> deadEnds = new HashSet<>();
	private ImmutableSet.Builder<Point2D> outerPointsBuilder = ImmutableSet.builder();
	private ImmutableSet<Point2D> exitsOnCycles;
	private final int maxNumOfStartPoints;
	private double secondaryRoadNetworkRoadLengthDeviation;
	public double v;
	private Set<DirectionFromPoint> filamentEnds;

	/**
	 * Returns a set points where secondary road network is connected with the cycle.
	 *
	 * @return
	 */
	public ImmutableSet<Point2D> exitsOnCycles() {
		return exitsOnCycles;
	}

	/**
	 * @param graph
	 * 	A preconstructed graph of low level roads, constructed by {@link City#constructCityCellGraph(org.tendiwa.graphs.MinimalCycle,
	 *    java.util.Set, java.util.Collection)}
	 * @param minimalCycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @param filamentEdges
	 * 	A collection of all the edges of a {@link org.tendiwa.settlements.City#lowLevelRoadGraph} that are not
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
	 * 	A seeded {@link java.util.Random} used to generate the parent {@link City}.
	 */
	NetworkWithinCycle(
		SimpleGraph<Point2D, Segment2D> graph,
		MinimalCycle<Point2D, Segment2D> minimalCycle,
		Collection<Segment2D> filamentEdges,
		int roadsFromPoint,
		double roadSegmentLength,
		double snapSize,
		double connectivity,
		double secondaryRoadNetworkDeviationAngle,
		double secondaryRoadNetworkRoadLengthDeviation,
		int maxNumOfStartPoints,
		Random random,
		TestCanvas canvas
	) {
		this.minimalCycle = minimalCycle;
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
		this.canvas = canvas;
		secRoadNetwork = new SimpleGraph<>(graph.getEdgeFactory());

		for (Point2D vertex : relevantNetwork.vertexSet()) {
			deadEnds.add(vertex);
		}


		Coordinate[] coordinates = pointListToCoordinateArray(minimalCycle.vertexList());
		// TODO: Are all cycles counter-clockwise? (because of the MCB algorithm)
		if (!CGAlgorithms.isCCW(coordinates)) {
			List<Coordinate> list = Arrays.asList(coordinates);
			Collections.reverse(list);
			ring = list.toArray(new Coordinate[list.size()]);
		} else {
			ring = coordinates;
		}
		isCycleClockwise = false;
		cycleNodes = new HashSet<>(minimalCycle.vertexList());

		buildSegment2DNetwork(minimalCycle);

		exitsOnCycles = outerPointsBuilder.build();

		blockDivision = new DivisionOfSpaceInsideCycleIntoBlocks(
			relevantNetwork,
			filamentEnds,
			roadSegmentLength+secondaryRoadNetworkRoadLengthDeviation,
			canvas
		) ;
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
		for (Segment2D road : startingRoads(cycle)) {
			Point2D sourceNode = calculateDeviatedMidPoint(road);
			// Made dead end so two new roads are not inserted to network.
			deadEnds.add(sourceNode);
			insertNode(road, sourceNode);
			// Made not-dead end so a road can be placed from it.
			deadEnds.remove(sourceNode);
			double direction = deviatedBoundaryPerpendicular(road);
			Point2D newNode = tryPlacingRoad(sourceNode, direction);
			if (newNode != null && !isDeadEnd(newNode)) {
				nodeQueue.push(new DirectionFromPoint(newNode, direction));
				deadEnds.add(sourceNode);
				outerPointsBuilder.add(sourceNode);
			}
		}
		Set<DirectionFromPoint> filamentEnds = new HashSet<>();
		while (!nodeQueue.isEmpty()) {
			DirectionFromPoint node = nodeQueue.removeLast();
			boolean addedAnySegments = false;
			for (int i = 1; i < roadsFromPoint; i++) {
				double newDirection = deviateDirection(node.direction + Math.PI + i * (Math.PI * 2 / roadsFromPoint));
				Point2D newNode = tryPlacingRoad(node.node, newDirection);
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

	private ImmutableSet<Point2D> nodes2TheirPoints(Set<DirectionFromPoint> filamentEnds) {
		ImmutableSet.Builder<Point2D> builder = ImmutableSet.builder();
		for (DirectionFromPoint node : filamentEnds) {
			builder.add(node.node);
		}
		return builder.build();
	}

	private Set<DirectionFromPoint> removeMultidegreeFilamentEnds(Set<DirectionFromPoint> nodes) {
		Iterator<DirectionFromPoint> iterator = nodes.iterator();
		for (
			DirectionFromPoint point = iterator.next();
			iterator.hasNext();
			point = iterator.next()
			) {
			if (relevantNetwork.degreeOf(point.node) != 1) {
				iterator.remove();
			}
		}
		return nodes;
	}

	private boolean isDeadEnd(Point2D node) {
		return deadEnds.contains(node);
	}

	/**
	 * Returns a slightly changed direction.
	 *
	 * @param newDirection
	 * 	Original angle in radians.
	 * @return Slightly changed angle in radians.
	 */

	private double deviateDirection(double newDirection) {
		v = random.nextDouble();
		return newDirection - secondaryRoadNetworkDeviationAngle + v * secondaryRoadNetworkDeviationAngle * 2;
	}

	private double deviatedLength(double roadSegmentLength) {
		return roadSegmentLength - secondaryRoadNetworkRoadLengthDeviation / 2 + random.nextDouble() *
			secondaryRoadNetworkRoadLengthDeviation;
	}

	/**
	 * [Kelly figure 42]
	 *
	 * @param edge
	 * 	An edge of {@link City#lowLevelRoadGraph}.
	 * @return An angle in radians perpendicular to {@code edge}. The angle is probably slightly deviated.
	 */
	private double deviatedBoundaryPerpendicular(Segment2D edge) {
		// TODO: Actually deviate the angle
		double angle = edge.start.angleTo(edge.end);
		return deviateDirection(angle + Math.PI / 2
			* (isCycleClockwise ? -1 : 1)
			* (isStartBeforeEndInRing(new Coordinate(edge.start.x, edge.start.y), new Coordinate(edge.end.x, edge.end.y)) ? 1 : -1));
	}


	/**
	 * Checks if one coordinate appears earlier in looped {@link #ring}.
	 *
	 * @param start
	 * 	One coordinate.
	 * @param end
	 * 	Another coordinate.
	 * @return true if {@code start} appears earlier, false otherwise.
	 */
	private boolean isStartBeforeEndInRing(Coordinate start, Coordinate end) {
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
	private Point2D tryPlacingRoad(Point2D source, double direction) {
		assert !isDeadEnd(source);
		double roadLength = deviatedLength(roadSegmentLength);
		double dx = roadLength * Math.cos(direction);
		double dy = roadLength * Math.sin(direction);
		Point2D targetNode = new Point2D(source.x + dx, source.y + dy);
		SnapEvent snapEvent = new SnapTest(snapSize, source, targetNode, relevantNetwork, canvas).snap();
		if (source.equals(snapEvent.targetNode)) {
			assert false;
		}
		switch (snapEvent.eventType) {
			case NO_SNAP:
				assert targetNode == snapEvent.targetNode;
				if (!relevantNetwork.addVertex(targetNode)) {
					assert false : targetNode;
					return null;
				}
				addRoad(source, targetNode);
				return snapEvent.targetNode;
			case ROAD_SNAP:
				if (random.nextDouble() < connectivity) {
					if (!filamentEdges.contains(snapEvent.road)) {
						deadEnds.add(snapEvent.targetNode);
					}
					if (isDeadEnd(snapEvent.road.start) && isDeadEnd(snapEvent.road.end)) {
						deadEnds.add(snapEvent.targetNode);
					}
					insertNode(snapEvent.road, snapEvent.targetNode);
					addRoad(source, snapEvent.targetNode);
					return snapEvent.targetNode;
				} else {
					return null;
				}
			case NODE_SNAP:
				if (random.nextDouble() < connectivity) {
					if (isDeadEnd(snapEvent.targetNode) && isDeadEnd(source)) {
						return null;
					}
					addRoad(source, snapEvent.targetNode);
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
	private void addRoad(Point2D source, Point2D target) {
		relevantNetwork.addEdge(source, target);
//        if (source.distanceTo(target) < 0.5) {
//            System.out.println(source+" "+target);
//            throw new RuntimeException();
//        }
		if (!(isDeadEnd(source) && isDeadEnd(target))) {
			secRoadNetwork.addVertex(source);
			secRoadNetwork.addVertex(target);
			secRoadNetwork.addEdge(source, target);
			if (cycleNodes.contains(target)) {
				// Builder may contain the target point, but then it just won't be added.
				outerPointsBuilder.add(target);
			}
		}
	}

	/**
	 * Creates an unmodifiable view of {@link #secRoadNetwork}.
	 *
	 * @return An unmodifiable graph containing this NetworkWithinCycle's secondary road network.
	 */
	public UndirectedGraph<Point2D, Segment2D> network() {
		return new UnmodifiableUndirectedGraph<>(secRoadNetwork);
	}

	public MinimalCycle<Point2D, Segment2D> cycle() {
		return minimalCycle;
	}


	/**
	 * [Kelly figure 42]
	 * <p>
	 * Adds new node between two existing nodes, removing an existing road between them and placing 2 new roads. to
	 * road
	 * network. Since {@link org.tendiwa.settlements.RoadGraph} is immutable, new nodes are saved in a separate
	 * collection.
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
		assert !road.start.equals(point) : "point is start";
		assert !road.end.equals(point) : "point is end";
		assert road.start.distanceTo(point) > 0.1 : road.start.distanceTo(point) + " " + road.start.distanceTo(road.end);
		assert road.end.distanceTo(point) > 0.1 : road.end.distanceTo(point) + " " + road.start.distanceTo(road.end);
		relevantNetwork.removeEdge(road);
		relevantNetwork.addVertex(point);
		addRoad(road.start, point);
		addRoad(point, road.end);
		if (cycleNodes.contains(road.start) && cycleNodes.contains(road.end)) {
			cycleNodes.add(point);
			outerPointsBuilder.add(point);
		}
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
	 * Finds the roads of to start secondary road network generation from.
	 *
	 * @param cycle
	 * 	A MinimalCycle that contains this NetworkWithinCycle's secondary road network inside it.
	 * @return Several roads.
	 */
	private Collection<Segment2D> startingRoads(MinimalCycle<Point2D, Segment2D> cycle) {
		List<Segment2D> edges = Lists.newArrayList(cycle);
		Collections.sort(
			edges,
			// TODO: The fuck is signum doing here?
			(o1, o2) -> (int) Math.signum(o2.start.distanceTo(o2.end) - o1.start.distanceTo(o1.end))
		);
		int numberOfStartPoints = Math.min(maxNumOfStartPoints, minimalCycle.vertexList().size());
		return edges.subList(0, numberOfStartPoints);
	}

	public Set<MinimalCycle<Point2D, Segment2D>> getEnclosedBlocks() {
		return blockDivision.getBlocks();
	}

	public ImmutableSet<Point2D> filamentEnds() {
		return filamentEndPoints;
	}

	class DirectionFromPoint {
		final Point2D node;
		final double direction;

		DirectionFromPoint(Point2D node, double direction) {
			this.node = node;
			this.direction = direction;
		}
	}

}
