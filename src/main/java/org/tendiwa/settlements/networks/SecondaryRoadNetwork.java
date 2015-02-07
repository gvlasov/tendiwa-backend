package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.JTSUtils;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.geometry.extensions.straightSkeleton.Bisector;
import org.tendiwa.graphs.MinimalCycle;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.*;

final class SecondaryRoadNetwork {

	/**
	 * [Kelly figure 42]
	 * <p>
	 */
	private final int roadsFromPoint;
	private final double roadSegmentLength;
	private final double snapSize;
	private final double secondaryRoadNetworkRoadLengthDeviation;
	private final double secondaryRoadNetworkDeviationAngle;
	private final HolderOfSplitCycleEdges holderOfSplitCycleEdges;
	private final boolean favourAxisAlignedSegments;
	private final Set<Point2D> deadEnds = new HashSet<>();
	private final UndirectedGraph<Point2D, Segment2D> relevantNetwork;
	/**
	 * Nodes that form the actual edges of the enclosing cycle of this {@link NetworkWithinCycle}.
	 */
	private final Set<Point2D> cycleVertices;
	private final CycleRing ring;
	private final RoadInserter roadInserter;
	private final MinimalCycle<Point2D, Segment2D> originalMinimalCycle;
	private final Set<Point2D> startingPoints;

	ImmutableSet<Point2D> filamentEndPoints;
	Set<DirectionFromPoint> filamentEnds;
	private final UndirectedGraph<Point2D, Segment2D> originalRoadGraph;
	private final Collection<MinimalCycle<Point2D, Segment2D>> enclosedCycles;
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
		Collection<MinimalCycle<Point2D, Segment2D>> enclosedCycles,
		Random random
	) {
		this.snapSize = snapSize;
		this.secondaryRoadNetworkRoadLengthDeviation = secondaryRoadNetworkRoadLengthDeviation;
		this.secondaryRoadNetworkDeviationAngle = secondaryRoadNetworkDeviationAngle;
		this.holderOfSplitCycleEdges = holderOfSplitCycleEdges;
		this.originalRoadGraph = originalRoadGraph;
		this.enclosedCycles = enclosedCycles;
		this.random = random;
		this.roadsFromPoint = roadsFromPoint;
		this.roadSegmentLength = roadSegmentLength;
		this.favourAxisAlignedSegments = favourAxisAlignedSegments;
		this.relevantNetwork = relevantNetwork;
		this.originalMinimalCycle = originalMinimalCycle;

		this.cycleVertices = new LinkedHashSet<>(originalMinimalCycle.vertexList());
		updateRelevantNetworkWithNeighborCyclesSplitEdges();
		ring = new CycleRing(originalMinimalCycle);
		this.roadInserter = new RoadInserter(
			relevantNetwork,
			originalRoadGraph,
			cycleVertices,
			outerPointsBuilder,
			deadEnds,
			holderOfSplitCycleEdges,
			filamentEdges,
			roadSegmentLength,
			snapSize,
			connectivity,
			secondaryRoadNetworkRoadLengthDeviation,
			random
		);

		startingPoints = snapAndInsertStartingPoints();
		TestCanvas.canvas.drawAll(startingPoints, DrawingPoint2D.withColorAndSize(Color.red, 5));
		buildSegment2DNetwork();
		exitsOnCycles = outerPointsBuilder.build();
	}

	private Set<Point2D> snapAndInsertStartingPoints() {
		Map<Segment2D, List<Point2D>> pointsOnPolygonBorder = IntervalsAlongPolygonBorder.compute(
			originalMinimalCycle.vertexList(),
			roadSegmentLength,
			secondaryRoadNetworkRoadLengthDeviation,
			originalRoadGraph::getEdge,
			random
		);
		int numberOfPoints = (int) pointsOnPolygonBorder.values().stream().flatMap
			(Collection::stream).count();
		Set<Point2D> points = new LinkedHashSet<>(numberOfPoints);
		for (Segment2D edge : pointsOnPolygonBorder.keySet()) {
			for (Point2D startingPoint : pointsOnPolygonBorder.get(edge)) {
				Point2D segmentEndToSnap = findSegmentEndToSnap(startingPoint, edge);
				if (segmentEndToSnap == null) {
					points.add(startingPoint);
					Segment2D actualEdge = holderOfSplitCycleEdges.findActualEdge(edge, startingPoint);
					roadInserter.insertStartingPoint(actualEdge, startingPoint);
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
		if (toStart < toEnd) {
			if (toStart < snapSize) {
				return edge.start;
			}
		} else {
			if (toEnd < snapSize) {
				return edge.end;
			}
		}
		return null;
	}

	private void updateRelevantNetworkWithNeighborCyclesSplitEdges() {
		Set<Segment2D> relevantNetworkEdges = ImmutableSet.copyOf(relevantNetwork.edgeSet());
		relevantNetworkEdges.stream()
			.filter(holderOfSplitCycleEdges::isEdgeSplit)
			.forEach(edge -> {
				relevantNetwork.removeEdge(edge);
				UndirectedGraph<Point2D, Segment2D> splitEdgeGraph = holderOfSplitCycleEdges.getGraph(edge);
				splitEdgeGraph.vertexSet().forEach((v) -> {
					relevantNetwork.addVertex(v);
					cycleVertices.add(v);
				});
				for (Segment2D splitEdge : splitEdgeGraph.edgeSet()) {
					relevantNetwork.addEdge(splitEdge.start, splitEdge.end, splitEdge);
				}
			});
	}

	/**
	 * [Kelly figure 42]
	 * <p>
	 * Calculates initial road segments and processes road growth. Also finds out which nodes are secondary network's
	 * filament ends, if there are any.
	 */
	private void buildSegment2DNetwork() {
		new SegmentFloodFill().fill();
		properlyConnectEnclosedCyclesWithRestOfNetwork();
	}

	public UndirectedGraph<Point2D, Segment2D> getSecondaryRoadGraph() {
		return roadInserter.secRoadNetwork;
	}

	/**
	 * Flood-fills a {@link org.tendiwa.graphs.MinimalCycle} with the secondary road network's segments.
	 */
	private class SegmentFloodFill {
		private final Deque<DirectionFromPoint> nodeQueue;
		private final Set<DirectionFromPoint> filamentEnds = new LinkedHashSet<>();

		SegmentFloodFill() {
			nodeQueue = new ArrayDeque<>();
		}

		void fill() {
			startFloodFillFromStartingPoints();
//			eliminateUnfilledBlocks();
			SecondaryRoadNetwork.this.filamentEnds = removeMultidegreeFilamentEnds(this.filamentEnds);
			filamentEndPoints = nodes2TheirPoints(this.filamentEnds);
		}

		/**
		 * Finds chains on the enclosing cycle that contain several starting points. Having such chains means that a
		 * block containing that chain can be further divided into more blocks. Such unfilled blocks are caused by
		 * the network's cycle and enclosed cycles forming thin necks where flood-filling can't enter.
		 */
		private void eliminateUnfilledBlocks() {
			UnfilledBlocksDetector unfilledBlocksDetector = createUnfilledBlocksDetector();
			while (unfilledBlocksDetector.canOfferStartingPoint()) {
				if (unfilledBlocksDetector.canOfferStartingPoint()) {
					floodFillFromPoint(unfilledBlocksDetector.getStartingPoint(), false);
					unfilledBlocksDetector.update();
				}
			}
		}

		/**
		 * Starts flood-filling innards of the cycle from no greater than {@code maxNumOfStartPoints} points.
		 */
		private void startFloodFillFromStartingPoints() {
			for (Point2D sourceNode : startingPoints) {
				if (relevantNetwork.degreeOf(sourceNode) > 2) {
					// Flood filling already came to this point.
					continue;
				}
				floodFillFromPoint(sourceNode, true);
				while (!nodeQueue.isEmpty()) {
					spanSecondaryNetworkFromQueuedNodes();
				}
			}
		}

		private UnfilledBlocksDetector createUnfilledBlocksDetector() {
			return new UnfilledBlocksDetector(
				startingPoints,
				relevantNetwork,
				cycleVertices
			);
		}

		private SnapEvent floodFillFromPoint(
			Point2D sourceNode,
			boolean prohibitSnappingRightAway
		) {
			double direction;
//			if (!relevantNetwork.containsVertex(sourceNode)) {
//				Segment2D road = getSplitRoadWherePointIs(sourceNode, startingPointsToOriginalRoads.get(sourceNode));
//				// Made dead end so two new roads are not inserted to the network.
////				deadEnds.add(sourceNode);
////				insertNode(road, sourceNode);
////				// Made not-dead end so a road can be placed from it.
////				deadEnds.remove(sourceNode);
//				Segment2D originalEdge = holderOfSplitCycleEdges.findOriginalEdge(road);
//				direction = deviatedBoundaryPerpendicular(originalEdge);
//			} else {
			direction = deviatedAngleBisector(sourceNode);
			assert roadInserter.isDeadEnd(sourceNode);
			deadEnds.remove(sourceNode);
//			}

			SnapEvent snapEvent = roadInserter.tryPlacingRoad(sourceNode, direction, prohibitSnappingRightAway);
			if (doesNotSnapToDeadEnd(snapEvent)) {
				nodeQueue.push(new DirectionFromPoint(snapEvent.targetNode, direction));
				outerPointsBuilder.add(sourceNode);
			}
			deadEnds.add(sourceNode);
			return snapEvent;
		}

		private double deviatedAngleBisector(Point2D enclosingCycleNode) {
			PairOfClockwiseAdjacentCycleEdges clockwisePair = new PairOfClockwiseAdjacentCycleEdges(enclosingCycleNode);
			Bisector bisector = new Bisector(
				clockwisePair.previous,
				clockwisePair.next,
				enclosingCycleNode,
				true
			);
			Segment2D bisectorSegment = bisector.asSegment(40);
			return bisectorSegment.start.angleTo(bisectorSegment.end);
		}

		/**
		 * Finds a pair of edges of {@link #relevantNetwork} that share a common vertex, go clockwise, are part of
		 * the network's enclosing cycle, and the next starts where the previous ends.
		 */
		private final class PairOfClockwiseAdjacentCycleEdges {
			final Segment2D previous;
			final Segment2D next;

			PairOfClockwiseAdjacentCycleEdges(Point2D vertex) {
				assert relevantNetwork.degreeOf(vertex) == 2 : relevantNetwork.degreeOf(vertex);
				assert cycleVertices.contains(vertex);
				Segment2D previousEdge = null, nextEdge = null;
				for (Segment2D edge : relevantNetwork.edgesOf(vertex)) {
					Point2D anotherEnd = edge.anotherEnd(vertex);
					if (cycleVertices.contains(anotherEnd)) {
						if (previousEdge == null) {
							previousEdge = edge;
						} else {
							nextEdge = edge;
						}
					}
				}
				assert previousEdge != null && nextEdge != null;
				previousEdge = revertIfNecessary(previousEdge);
				nextEdge = revertIfNecessary(nextEdge);
				if (previousEdge.start == nextEdge.end) {
					Segment2D buf = previousEdge;
					previousEdge = nextEdge;
					nextEdge = buf;
				} else {
					assert previousEdge.end == nextEdge.start;
				}
				previous = previousEdge;
				next = nextEdge;
				assert nextEdge.start == previousEdge.end;
			}

			private Segment2D revertIfNecessary(Segment2D edge) {
				Segment2D originalEdge = holderOfSplitCycleEdges.findOriginalEdge(edge);
				if (
					(Math.signum(originalEdge.dx()) != Math.signum(edge.dx())
						|| Math.signum(originalEdge.dy()) != Math.signum(edge.dy()))
						^ ring.isStartBeforeEndInRing(originalEdge)
					) {
					return edge.reverse();
				}
				return edge;
			}
		}

		private void spanSecondaryNetworkFromQueuedNodes() {
			DirectionFromPoint node = nodeQueue.removeLast();
			boolean addedAnySegments = false;
			for (int i = 1; i < roadsFromPoint; i++) {
				double newDirection = deviateDirection(node.direction + Math.PI + i * (Math.PI * 2 / roadsFromPoint));
				SnapEvent snapEvent = roadInserter.tryPlacingRoad(node.node, newDirection, false);
				if (snapEvent == null) {
					continue;
				}
				Point2D newNode = snapEvent.targetNode;
				if (newNode != null && !roadInserter.isDeadEnd(newNode) && snapEvent.eventType != SnapEventType
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


	private void properlyConnectEnclosedCyclesWithRestOfNetwork() {
		for (MinimalCycle<Point2D, Segment2D> cycle : enclosedCycles) {
			EnclosedCycleConnections enclosedCycleConnections = new EnclosedCycleConnections(cycle).compute();
			switch (enclosedCycleConnections.getNumberOfCycleConnections()) {
				case 1:
					addMissingConnectionToEnclosedCycle(cycle, enclosedCycleConnections.getConnectionPoint());
					break;
				case 0:
					addTwoMissingConnectionsToEnclosedCycle(cycle);
			}
		}
	}

	private void addTwoMissingConnectionsToEnclosedCycle(MinimalCycle<Point2D, Segment2D> cycle) {
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
		roadInserter.tryPlacingRoadFromEnclosedCycle(
			points.get(Utils.previousIndex(leastPointIndex, size)),
			points.get(leastPointIndex),
			points.get(Utils.nextIndex(leastPointIndex, size)),
			reflex
		);
		roadInserter.tryPlacingRoadFromEnclosedCycle(
			points.get(Utils.previousIndex(greatestPointIndex, size)),
			points.get(greatestPointIndex),
			points.get(Utils.nextIndex(greatestPointIndex, size)),
			reflex
		);
	}

	private void addMissingConnectionToEnclosedCycle(MinimalCycle<Point2D, Segment2D> cycle, Point2D connectionPoint) {
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
		roadInserter.tryPlacingRoadFromEnclosedCycle(
			polygon.get(preFarthestPointIndex),
			polygon.get(farthestPointIndex),
			polygon.get(postFarthestPointIndex),
			reflexDirection
		);
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

	private boolean doesNotSnapToDeadEnd(SnapEvent snapEvent) {
		return snapEvent != null && snapEvent.targetNode != null && !roadInserter.isDeadEnd(snapEvent.targetNode);
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
	 * Finds out how many edges connect a cycle with the rest of the network.
	 */
	private class EnclosedCycleConnections {
		private final MinimalCycle<Point2D, Segment2D> cycle;
		private int numberOfCycleConnections;
		private Point2D connectionPoint;

		public EnclosedCycleConnections(MinimalCycle<Point2D, Segment2D> cycle) {
			this.cycle = cycle;
		}

		public int getNumberOfCycleConnections() {
			return numberOfCycleConnections;
		}

		public Point2D getConnectionPoint() {
			return connectionPoint;
		}

		/**
		 * Finds the number of points where an enclosed cycle in connected with other parts of the secondary road
		 * network, and remembers one of those points.
		 */
		public EnclosedCycleConnections compute() {
			numberOfCycleConnections = 0;
			connectionPoint = null;
			int twiceExtraVertices = 0;
			for (Segment2D segment : cycle) {
				if (holderOfSplitCycleEdges.isEdgeSplit(segment)) {
					UndirectedGraph<Point2D, Segment2D> graph = holderOfSplitCycleEdges.getGraph(segment);
					for (Point2D vertex : graph.vertexSet()) {
						if (relevantNetwork.degreeOf(vertex) > 2) {
							numberOfCycleConnections++;
							if (graph.degreeOf(vertex) == 1) {
								// Degrees at vertices of split edge graphs that have degree of 1 will be added twice because
								// those vertices are in two neighbor graphs in holderOfSplitCycleEdges.
								twiceExtraVertices++;
							}
							connectionPoint = vertex;
						}
					}
					numberOfCycleConnections -= twiceExtraVertices;
				} else {
					for (Point2D vertex : new Point2D[]{segment.start, segment.end}) {
						if (relevantNetwork.degreeOf(vertex) > 2) {
							numberOfCycleConnections++;
							twiceExtraVertices++;
							connectionPoint = vertex;
						}
					}
				}
			}
			assert twiceExtraVertices % 2 == 0;
			numberOfCycleConnections -= twiceExtraVertices / 2;
			return this;
		}
	}
}
