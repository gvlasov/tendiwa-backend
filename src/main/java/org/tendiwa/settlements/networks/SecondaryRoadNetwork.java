package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.JTSUtils;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.CommonEdgeSplitter;
import org.tendiwa.graphs.GraphCycleTraversal;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

final class SecondaryRoadNetwork {

	private final OrientedCycle enclosingCycle;
	private final UndirectedGraph<Point2D, Segment2D> fullGraph;
	private final SegmentInserter segmentInserter;
	private final Set<Point2D> startingPoints;
	private final UndirectedGraph<Point2D, Segment2D> graph;

	ImmutableSet<Point2D> filamentEndPoints;
	Set<DirectionFromPoint> filamentEnds;
	private final Collection<OrientedCycle> enclosedCycles;
	private final NetworkGenerationParameters networkGenerationParameters;
	private final Random random;

	SecondaryRoadNetwork(
		UndirectedGraph<Point2D, Segment2D> fullGraph,
		OrientedCycle enclosingCycle,
		Collection<OrientedCycle> enclosedCycles,
		CommonEdgeSplitter<Point2D, Segment2D> commonEdgeSplitter,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {
		this.enclosingCycle = enclosingCycle;
		this.enclosedCycles = enclosedCycles;
		this.networkGenerationParameters = networkGenerationParameters;
		this.random = random;
		this.fullGraph = fullGraph;

		this.graph = PlanarGraphs.createGraph();
		this.segmentInserter = new SegmentInserter(
			fullGraph,
			graph,
			enclosingCycle,
			enclosedCycles,
			commonEdgeSplitter,
			networkGenerationParameters,
			random
		);

		startingPoints = segmentInserter.snapAndInsertStartingPoints(
			computePointsOnPolygonBorder()
		);
		TestCanvas.canvas.drawAll(startingPoints, DrawingPoint2D.withColorAndSize(Color.red, 5));

		new SegmentFloodFill().fill();
		addMissingConnectionsWithEnclosedCycles();
	}

	private Map<Segment2D, List<Point2D>> computePointsOnPolygonBorder() {
		List<Point2D> vertexList = GraphCycleTraversal
			.traverse(enclosingCycle.graph())
			.startingWith(enclosingCycle.graph().vertexSet().stream().findAny().get())
			.stream()
			.map(GraphCycleTraversal.NeighborsTriplet::current)
			.collect(Collectors.toList());
		return IntervalsAlongPolygonBorder.compute(
			vertexList,
			networkGenerationParameters.roadSegmentLength,
			networkGenerationParameters.secondaryNetworkSegmentLengthDeviation,
			enclosingCycle.graph()::getEdge,
			random
		);
	}

	public UndirectedGraph<Point2D, Segment2D> getGraph() {
		return graph;
	}

	/**
	 * Flood-fills space inside {@link OrientedCycle#graph()} with the secondary road
	 * network's segments.
	 */
	private class SegmentFloodFill {
		private final Deque<DirectionFromPoint> nodeQueue;
		private final Set<DirectionFromPoint> filamentEnds = new LinkedHashSet<>();

		SegmentFloodFill() {
			nodeQueue = new ArrayDeque<>();
		}

		void fill() {
			floodFillFromStartingPoints();
//			eliminateUnfilledBlocks();
			SecondaryRoadNetwork.this.filamentEnds = removeMultidegreeFilamentEnds(this.filamentEnds);
			filamentEndPoints = nodes2TheirPoints(this.filamentEnds);
		}

		/**
		 * Finds chains on the enclosing cycle that contain several starting points. Having such chains means that a
		 * block containing that chain can be further divided into more blocks. Such unfilled blocks are caused by
		 * the network's cycle and enclosed cycles forming thin necks where flood-filling can't enter.
		 */
//		private void eliminateUnfilledBlocks() {
//			UnfilledBlocksDetector unfilledBlocksDetector = createUnfilledBlocksDetector();
//			while (unfilledBlocksDetector.canOfferStartingPoint()) {
//				if (unfilledBlocksDetector.canOfferStartingPoint()) {
//					floodFillFromPoint(unfilledBlocksDetector.getStartingPoint(), false);
//					unfilledBlocksDetector.update();
//				}
//			}
//		}

//		private UnfilledBlocksDetector createUnfilledBlocksDetector() {
//			return new UnfilledBlocksDetector(
//				startingPoints,
//				fullGraph,
//				cycleVertices
//			);
//		}

		/**
		 * Starts flood-filling innards of the cycle from no greater than {@code maxNumOfStartPoints} points.
		 */
		private void floodFillFromStartingPoints() {
			for (Point2D sourceNode : startingPoints) {
				if (fullGraph.degreeOf(sourceNode) > 2) {
					// Flood filling already came to this point.
					continue;
				}
				floodFillFromPoint(sourceNode, true);
				while (!nodeQueue.isEmpty()) {
					spanSecondaryNetworkFromQueuedNodes();
				}
			}
		}

		private SnapEvent floodFillFromPoint(
			Point2D sourceNode,
			boolean prohibitSnappingRightAway
		) {
			double direction = enclosingCycle.deviatedAngleBisector(sourceNode, true);
			assert segmentInserter.isDeadEnd(sourceNode);

			SnapEvent snapEvent = segmentInserter.tryPlacingRoad(sourceNode, direction, prohibitSnappingRightAway);
			if (doesNotSnapToDeadEnd(snapEvent)) {
				nodeQueue.push(new DirectionFromPoint(snapEvent.targetNode, direction));
			}
			return snapEvent;
		}

		private void spanSecondaryNetworkFromQueuedNodes() {
			DirectionFromPoint node = nodeQueue.removeLast();
			boolean addedAnySegments = false;
			for (int i = 1; i < networkGenerationParameters.roadsFromPoint; i++) {
				double newDirection = deviateDirection(node.direction + Math.PI + i * (Math.PI * 2 /
					networkGenerationParameters.roadsFromPoint));
				SnapEvent snapEvent = segmentInserter.tryPlacingRoad(node.node, newDirection, false);
				if (snapEvent == null) {
					continue;
				}
				Point2D newNode = snapEvent.targetNode;
				if (newNode != null && !segmentInserter.isDeadEnd(newNode) && snapEvent.eventType != SnapEventType
					.NODE_SNAP) {
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
	}

	private void addMissingConnectionsWithEnclosedCycles() {
		Set<Point2D> secondaryNetworkVertices = graph.vertexSet();
		for (OrientedCycle cycle : enclosedCycles) {
			Set<Point2D> connections = Sets.intersection(
				cycle.graph().vertexSet(),
				secondaryNetworkVertices
			);
			if (connections.size() == 1) {
				addMissingConnectionToEnclosedCycle(cycle, connections.iterator().next());
			} else if (connections.size() == 0) {
				addTwoMissingConnectionsToEnclosedCycle(cycle);
			}
		}
	}

	private void addTwoMissingConnectionsToEnclosedCycle(OrientedCycle cycle) {
		List<Point2D> points = cycle.vertexList();
		int leastPointIndex,
			greatestPointIndex;
		boolean reflex = JTSUtils.isYDownCCW(points);
		leastPointIndex = greatestPointIndex = 0;
		int size = points.size();
		if (random.nextBoolean()) {
			for (int i = 1; i < size; i++) {
				Point2D point = points.get(i);
				if (points.get(leastPointIndex).y > point.y) {
					leastPointIndex = i;
				} else if (points.get(greatestPointIndex).y < point.y) {
					greatestPointIndex = i;
				}
			}
		} else {
			for (int i = 1; i < size; i++) {
				Point2D point = points.get(i);
				if (points.get(leastPointIndex).x > point.x) {
					leastPointIndex = i;
				} else if (points.get(greatestPointIndex).x < point.x) {
					greatestPointIndex = i;
				}
			}
		}
		segmentInserter.tryPlacingRoadFromEnclosedCycle(
			points.get(Utils.previousIndex(leastPointIndex, size)),
			points.get(leastPointIndex),
			points.get(Utils.nextIndex(leastPointIndex, size)),
			reflex
		);
		segmentInserter.tryPlacingRoadFromEnclosedCycle(
			points.get(Utils.previousIndex(greatestPointIndex, size)),
			points.get(greatestPointIndex),
			points.get(Utils.nextIndex(greatestPointIndex, size)),
			reflex
		);
	}

	private void addMissingConnectionToEnclosedCycle(OrientedCycle cycle, Point2D connectionPoint) {
		assert connectionPoint != null;
		List<Point2D> points = cycle.vertexList();
		boolean reflexDirection = JTSUtils.isYDownCCW(points);
		Polygon polygon = new Polygon(points);
		int size = polygon.size();
		double maxDistanceSquared = 0;
		int farthestPointIndex = -1;
		for (int i = 0; i < size; i++) {
			Point2D vertex = polygon.get(i);
			double distanceSquared = connectionPoint.squaredDistanceTo(vertex);
			if (distanceSquared > maxDistanceSquared) {
				maxDistanceSquared = distanceSquared;
				farthestPointIndex = i;
			}
		}
		int preFarthestPointIndex = Utils.previousIndex(farthestPointIndex, size);
		int postFarthestPointIndex = Utils.nextIndex(farthestPointIndex, size);
		segmentInserter.tryPlacingRoadFromEnclosedCycle(
			polygon.get(preFarthestPointIndex),
			polygon.get(farthestPointIndex),
			polygon.get(postFarthestPointIndex),
			reflexDirection
		);
	}

	/**
	 * Returns a slightly changed direction.
	 * <p>
	 * If {@link org.tendiwa.settlements.networks.NetworkGenerationParameters#favourAxisAlignedSegments} is true, then
	 * the answer will be tilted towards the closest
	 * {@code Math.PI/2*n} angle.
	 *
	 * @param newDirection
	 * 	Original angle in radians.
	 * @return Slightly changed angle in radians. Answer is not constrained to [0; 2*PI] interval — it may be any
	 * number.
	 */
	private double deviateDirection(double newDirection) {
		double v = random.nextDouble();
		if (networkGenerationParameters.favourAxisAlignedSegments) {
			double closestAxisParallelDirection = Math.round(newDirection / (Math.PI / 2)) * (Math.PI / 2);
			if (Math.abs(closestAxisParallelDirection - newDirection) < networkGenerationParameters.secondaryRoadNetworkDeviationAngle) {
				return closestAxisParallelDirection;
			} else {
				return newDirection + networkGenerationParameters.secondaryRoadNetworkDeviationAngle * Math.signum
					(closestAxisParallelDirection
						- newDirection);
			}
		} else {
			return newDirection - networkGenerationParameters.secondaryRoadNetworkDeviationAngle + v *
				networkGenerationParameters.secondaryRoadNetworkDeviationAngle * 2;
		}
	}

	private boolean doesNotSnapToDeadEnd(SnapEvent snapEvent) {
		return snapEvent != null && snapEvent.targetNode != null && !segmentInserter.isDeadEnd(snapEvent.targetNode);
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
			if (fullGraph.degreeOf(point.node) != 1) {
				iterator.remove();
			}
		} while (iterator.hasNext());
		return nodes;
	}
}
