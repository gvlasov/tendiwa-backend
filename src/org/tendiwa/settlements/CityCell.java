package org.tendiwa.settlements;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

import java.util.*;

/**
 * [Kelly section 4.3.1]
 * <p/>
 * A part of a city bounded by a fundamental basis cycle (one of those in <i>minimal cycle basis</i> from [Kelly section
 * 4.3.1, figure 41].
 */
public class CityCell {
private final SimpleGraph<Point2D, Line2D> roadCycle;
/**
 * [Kelly figure 42]
 * <p/>
 */
private final int paramDegree;
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
private final int connectivity;
private final Random random;

/**
 * @param vertices
 * 	Vertices forming a single cycle.
 * @param paramDegree
 * @param snapSize
 */
CityCell(List<Point2D> vertices, int paramDegree, double roadSegmentLength, double snapSize, int connectivity, Random random) {
	this.paramDegree = paramDegree;
	this.roadSegmentLength = roadSegmentLength;
	this.snapSize = snapSize;
	this.connectivity = connectivity;
	this.random = random;

	Collection<Line2D> edges = new ArrayList<>(vertices.size());
	int l = vertices.size() - 1;
	for (int i = 0; i < l; i++) {
		edges.add(new Line2D(vertices.get(i), vertices.get(i + 1)));
	}
	edges.add(new Line2D(vertices.get(l), vertices.get(0)));

	roadCycle = new SimpleGraph<>(new EdgeFactory<Point2D, Line2D>() {
		@Override
		public Line2D createEdge(Point2D sourceVertex, Point2D targetVertex) {
			return new Line2D(sourceVertex, targetVertex);
		}
	});
	for (Point2D vertex : vertices) {
		roadCycle.addVertex(vertex);
	}
	for (Line2D edge : edges) {
		roadCycle.addEdge(edge.start, edge.end, edge);
	}
	ring = buildNodeRing(vertices);
	isCycleClockwise = determineCycleDirection(ring);

	assert new ConnectivityInspector<>(roadCycle).isGraphConnected();
	for (Point2D vertex : roadCycle.vertexSet()) {
		assert roadCycle.degreeOf(vertex) == 2;
	}
	buildSecondaryRoadNetwork();
}

/**
 * @param ring
 * @return
 */
private boolean determineCycleDirection(Coordinate[] ring) {
	return !CGAlgorithms.isCCW(ring);
}

/**
 * [Kelly figure 42]
 * <p/>
 * Calculates initial road segments
 *
 * @return
 */
private void buildSecondaryRoadNetwork() {
	Deque<SecondaryRoadNetworkNode> nodeQueue = new ArrayDeque<>();
	for (Line2D road : longestRoads()) {
		// Source node is the same as midpoint from [Kelly figure 42], since in this implementation points are inherently nodes.
		Point2D sourceNode = calculateDeviatedMidPoint(road);
		insertNode(road, sourceNode);
		double direction = deviatedBoundaryPerpendicular(sourceNode, road);
		Point2D newNode = tryPlacingRoad(sourceNode, direction);
		if (newNode != null) {
			nodeQueue.push(new SecondaryRoadNetworkNode(newNode, direction));
		}
	}
	while (!nodeQueue.isEmpty()) {
		SecondaryRoadNetworkNode node = nodeQueue.pop();
		for (int i = 0; i < paramDegree; i++) {
			double newDirection = deviateDirection(node.direction + i * (Math.PI * 2 / paramDegree));
			Point2D newNode = tryPlacingRoad(node.point, newDirection);
			if (newNode != null) {
				nodeQueue.push(new SecondaryRoadNetworkNode(newNode, newDirection));
			}
		}
	}
}

private double deviateDirection(double newDirection) {
	return newDirection;
}

/**
 * [Kelly figure 42]
 *
 * @param deviatedMidpoint
 * @return
 */
private double deviatedBoundaryPerpendicular(Point2D deviatedMidpoint, Line2D edge) {
	Coordinate sourceCoordinate = new Coordinate(deviatedMidpoint.x, deviatedMidpoint.y);
	double angle = edge.start.angleTo(edge.end);
	return angle + Math.PI / 2
		* (isCycleClockwise ? 1 : -1)
		* (isStartBeforeEndInRing(new Coordinate(edge.start.x, edge.start.y), new Coordinate(edge.end.x, edge.end.y)) ? 1 : -1);
}

private boolean isStartBeforeEndInRing(Coordinate start, Coordinate end) {
	for (int i = 0; i < ring.length; i++) {
		if (ring[i].equals(start)) {
			if (ring[i + 1].equals(end)) {
				return true;
			} else {
				assert ring[i - 1].equals(end);
				return false;
			}
		}
	}
	throw new RuntimeException();
}

private Coordinate[] buildNodeRing(List<Point2D> vertices) {
	Point2D sourceNode = vertices.get(0);
	Point2D currentNode = sourceNode;
	Point2D previousNode = null;
	Coordinate[] ring = new Coordinate[roadCycle.vertexSet().size() + 1];
	int i = 0;
	// Traverses all vertices starting from sourceNode until it comes back to sourceNode.
	do {
		Point2D nextNode = null;
		// Having all edges of degree 2 proves that this is a cycle.
		assert roadCycle.edgesOf(currentNode).size() == 2 : roadCycle.edgesOf(currentNode).size();
		for (Line2D edge : roadCycle.edgesOf(currentNode)) {
			if (previousNode == null) {
				if (roadCycle.getEdgeSource(edge) == sourceNode) {
					nextNode = roadCycle.getEdgeTarget(edge);
				} else {
					assert roadCycle.getEdgeTarget(edge) == sourceNode;
					nextNode = roadCycle.getEdgeSource(edge);
				}
				break;
			} else {
				Point2D edgeTarget = roadCycle.getEdgeTarget(edge);
				if (edgeTarget == previousNode) {
					continue;
				}
				Point2D edgeSource = roadCycle.getEdgeSource(edge);
				if (edgeSource == previousNode) {
					continue;
				}
				if (currentNode == edgeSource) {
					nextNode = edgeTarget;
				} else {
					assert currentNode == edgeTarget;
					nextNode = edgeSource;
				}
			}
		}
		assert nextNode != null;
		ring[i] = new Coordinate(currentNode.x, currentNode.y);
		i++;
		previousNode = currentNode;
		currentNode = nextNode;
	} while (currentNode != sourceNode);
	ring[i] = ring[0];
	assert i == roadCycle.vertexSet().size() : i + " " + roadCycle.vertexSet().size();
	return ring;
}

/**
 * [Kelly figure 42, function placeSegment]
 * <p/>
 * Tries adding a new road to the secondary road network graph.
 *
 * @param sourceNode
 * 	Start point of a new road.
 * @param direction
 * 	Angle of a road to x-axis.
 * @return The new node, or null if placing did not succeed.
 */
private Point2D tryPlacingRoad(Point2D sourceNode, double direction) {
	double roadLength = deviatedLength(this.roadSegmentLength);
	double dx = roadLength * Math.cos(direction);
	double dy = roadLength * Math.sin(direction);
	Point2D targetPoint = new Point2D(sourceNode.x + dx, sourceNode.y + dy);
	SnapEvent snapEvent = new SnapTest(snapSize, sourceNode, targetPoint, roadCycle).snap();
	assert !sourceNode.equals(snapEvent.targetPoint);
	switch (snapEvent.eventType) {
		case NO_SNAP:
			roadCycle.addVertex(targetPoint);
			roadCycle.addEdge(sourceNode, snapEvent.targetPoint);
			return snapEvent.targetPoint;
		case ROAD_SNAP:
			if (random.nextInt() < connectivity) {
				insertNode(snapEvent.road, snapEvent.targetPoint);
				roadCycle.addEdge(sourceNode, snapEvent.targetPoint);
				return snapEvent.targetPoint;
			} else {
				return null;
			}
		case NODE_SNAP:
			if (random.nextInt() < connectivity) {
				roadCycle.addVertex(snapEvent.targetPoint);
				System.out.println(roadCycle.containsVertex(sourceNode));
				System.out.println(roadCycle.containsVertex(snapEvent.targetPoint));
				roadCycle.addEdge(sourceNode, snapEvent.targetPoint);
				return snapEvent.targetPoint;
			} else {
				return null;
			}
		default:
			throw new RuntimeException();
	}
}

private double deviatedLength(double roadSegmentLength) {
	return roadSegmentLength;
}

/**
 * [Kelly figure 42]
 * <p/>
 * Adds new node between two existing nodes, removing an existing road between them and placing 2 new roads. to road
 * network. Since {@link org.tendiwa.settlements.RoadGraph} is immutable, new nodes are saved in a separate collection.
 *
 * @param road
 * 	A road from {@link #roadCycle} on which a node is being inserted.
 * @param point
 * 	A point on that road where the node resides.
 */
private void insertNode(Line2D road, Point2D point) {
	assert !road.start.equals(point) : "point is start";
	assert !road.end.equals(point) : "point is end";
	assert road.start.distanceTo(point) > 0.1 : road.start.distanceTo(point) + " " + road.start.distanceTo(road.end);
	assert road.end.distanceTo(point) > 0.1 : road.end.distanceTo(point) + " " + road.start.distanceTo(road.end);
	roadCycle.removeEdge(road);
	roadCycle.addVertex(point);
	roadCycle.addEdge(road.start, point);
	roadCycle.addEdge(point, road.end);
}

private Point2D calculateDeviatedMidPoint(Line2D road) {
	return new Point2D(
		road.start.x + (road.end.x - road.start.x) / 2,
		road.start.y + (road.end.y - road.start.y) / 2
	);
}

/**
 * [Kelly figure 42]
 * <p/>
 * Returns several of the longest roads.
 *
 * @return Several of the longest roads.
 */
private Collection<Line2D> longestRoads() {
	List<Line2D> edges = new ArrayList<>(roadCycle.edgeSet());
	Collections.sort(edges, new Comparator<Line2D>() {
		@Override
		public int compare(Line2D o1, Line2D o2) {
			return (int) Math.signum(o2.length() - o1.length());
		}
	});
	return edges.subList(0, 2);
}

class SecondaryRoadNetworkNode {
	private final Point2D point;
	private final double direction;

	SecondaryRoadNetworkNode(Point2D point, double direction) {
		this.point = point;
		this.direction = direction;
	}
}
}
