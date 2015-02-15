package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.graphs.GraphCycleTraversal;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.awt.Color;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

final class SecondaryRoadNetwork implements NetworkPart {

	private final OrientedCycle enclosingCycle;
	private final FullNetwork fullNetwork;
	private final SegmentInserter segmentInserter;
	private final Graph2D graph;

	ImmutableSet<Point2D> filamentEndPoints;
	Set<DirectionFromPoint> filamentEnds;
	private final Collection<OrientedCycle> enclosedCycles;
	private final NetworkGenerationParameters networkGenerationParameters;
	private final Random random;

	SecondaryRoadNetwork(
		FullNetwork fullNetwork,
		OrientedCycle enclosingCycle,
		Collection<OrientedCycle> enclosedCycles,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {
		this.enclosingCycle = enclosingCycle;
		this.enclosedCycles = enclosedCycles;
		this.networkGenerationParameters = networkGenerationParameters;
		this.random = random;
		this.fullNetwork = fullNetwork;

		this.graph = new Graph2D();
		this.segmentInserter = new SegmentInserter(
			fullNetwork,
			splitOriginalMesh,
			graph,
			networkGenerationParameters,
			random
		);

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
			networkGenerationParameters.segmentLength,
			networkGenerationParameters.secondaryNetworkSegmentLengthDeviation,
			enclosingCycle.graph()::getEdge,
			random
		);
	}

	@Override
	public void notify(CutSegment2D cutSegment) {

	}

	public Graph2D graph() {
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
			SecondaryRoadNetwork.this.filamentEnds = this.filamentEnds.stream()
				.filter(end -> fullNetwork.graph().degreeOf(end.node) == 1)
				.collect(Collectors.toSet());
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
//					startFloodFillFromPoint(unfilledBlocksDetector.getStartingPoint(), false);
//					unfilledBlocksDetector.update();
//				}
//			}
//		}

//		private UnfilledBlocksDetector createUnfilledBlocksDetector() {
//			return new UnfilledBlocksDetector(
//				startingPoints,
//				fullNetwork,
//				cycleVertices
//			);
//		}

		/**
		 * Starts flood-filling innards of the cycle from no greater than {@code maxNumOfStartPoints} points.
		 */
		private void floodFillFromStartingPoints() {
			Collection<Point2D> startingPoints = new CycleWithStartingPoints(
				fullNetwork,
				networkGenerationParameters
			).snapAndInsertStartingPoints(
				computePointsOnPolygonBorder()
			);
			TestCanvas.canvas.drawAll(startingPoints, DrawingPoint2D.withColorAndSize(Color.red, 5));
			for (Point2D sourceNode : startingPoints) {
				if (graph.containsVertex(sourceNode)) {
					assert graph.degreeOf(sourceNode) == 1;
					// Flood filling already came to this point.
					continue;
				}
				startFloodFillFromPoint(sourceNode);
				while (!nodeQueue.isEmpty()) {
					spanSecondaryNetworkFromQueuedNodes();
				}
			}
		}

		private SnapEvent startFloodFillFromPoint(Point2D sourceNode) {
			double direction = enclosingCycle.deviatedAngleBisector(sourceNode, true);
			assert segmentInserter.isDeadEnd(sourceNode);

			SnapEvent snapEvent = segmentInserter.tryPlacingRoad(sourceNode, direction, true);
			if (doesNotSnapToDeadEnd(snapEvent)) {
				nodeQueue.push(new DirectionFromPoint(snapEvent.targetNode, direction));
			}
			return snapEvent;
		}

		private void spanSecondaryNetworkFromQueuedNodes() {
			DirectionFromPoint node = nodeQueue.removeLast();
			boolean addedAnySegments = false;
			for (int i = 1; i < networkGenerationParameters.roadsFromPoint; i++) {
				double newDirection = deviateDirection(
					node.direction + Math.PI + i * (Math.PI * 2 / networkGenerationParameters.roadsFromPoint)
				);
				SnapEvent snapEvent = segmentInserter.tryPlacingRoad(node.node, newDirection, false);
				if (snapEvent == null) {
					continue;
				}
				Point2D newNode = snapEvent.targetNode;
				if (
					newNode != null
						&& !segmentInserter.isDeadEnd(newNode)
						&& snapEvent.eventType != SnapEventType.NODE_SNAP
					) {
					nodeQueue.push(new DirectionFromPoint(newNode, newDirection));
					addedAnySegments = true;
				}
			}
			if (!addedAnySegments) {
				// This does not guarantee that only degree 1 nodes will be added to filament ends,
				// but it culls most of the wrong vertices.
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
		Function<Point2D, Double> getCoordinate = random.nextBoolean() ? Point2D::getX : Point2D::getY;
		Comparator<Point2D> coordinateComparator = (
			a,
			b
		) -> (int) Math.signum(getCoordinate.apply(a) - getCoordinate.apply(b));
		Point2D leastPoint = cycle.graph()
			.vertexSet()
			.stream()
			.max(coordinateComparator)
			.get();
		Point2D greatestPoint = cycle.graph()
			.vertexSet()
			.stream()
			.min(coordinateComparator)
			.get();
		segmentInserter.tryPlacingRoad(
			leastPoint,
			cycle.deviatedAngleBisector(leastPoint, false),
			false
		);
		segmentInserter.tryPlacingRoad(
			greatestPoint,
			cycle.deviatedAngleBisector(greatestPoint, false),
			false
		);
	}

	private void addMissingConnectionToEnclosedCycle(OrientedCycle cycle, Point2D connectionPoint) {
		assert connectionPoint != null;
		assert cycle.graph().vertexSet().contains(connectionPoint);
		Point2D farthestPoint = cycle.graph()
			.vertexSet()
			.stream()
			.max((a, b) -> {
				double distanceSquaredA = connectionPoint.squaredDistanceTo(a);
				double distanceSquaredB = connectionPoint.squaredDistanceTo(b);
				return (int) Math.signum(distanceSquaredA - distanceSquaredB);
			}).get();
		segmentInserter.tryPlacingRoad(
			farthestPoint,
			cycle.deviatedAngleBisector(farthestPoint, false),
			false
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
}
