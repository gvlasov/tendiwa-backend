package org.tendiwa.geometry.smartMesh;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.awt.Color;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

/**
 * Inserts new segments into {@link org.tendiwa.geometry.smartMesh.FullNetwork} and its subnetworks.
 */
public class SegmentInserter {
	private final FullNetwork fullNetwork;
	private final NetworkPart secondaryNetwork;
	private final Graph2D splitOriginalMesh;
	private final NetworkGenerationParameters networkGenerationParameters;
	private final Random random;

	public SegmentInserter(
		FullNetwork fullNetwork,
		Graph2D splitOriginalGraph,
		NetworkPart secondaryNetwork,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {
		this.fullNetwork = fullNetwork;
		this.splitOriginalMesh = splitOriginalGraph;
		this.networkGenerationParameters = networkGenerationParameters;
		this.secondaryNetwork = secondaryNetwork;
		this.random = random;
	}


	/**
	 * [Kelly figure 42, function placeSegment]
	 * <p>
	 * Tries adding a new road to the secondary road network graph.
	 */
	SnapEvent tryPlacingRoad(Ray beginning) {
		double segmentLength = deviatedLength(networkGenerationParameters.segmentLength);
		SnapEvent snapEvent = new SnapTest(
			networkGenerationParameters.snapSize,
			beginning.start,
			beginning.placeEnd(segmentLength),
			fullNetwork.graph()
		).snap();
		if (snapEvent.createsNewSegment()) {
			snapEvent.integrateInto(fullNetwork, this);
		}
		return snapEvent;
	}

	/**
	 * Creates an edge between two vertices and adds that edge to relevant network parts:
	 * <ul>
	 * <li>{@link #fullNetwork}</li>
	 * <li>{@link #secondaryNetwork}</li>
	 * </ul>
	 *
	 * @param source
	 * 	Start of segment.
	 * @param target
	 * 	End of segment.
	 */
	void addSecondaryNetworkEdge(Point2D source, Point2D target) {
		assert !fullNetwork.graph().containsEdge(source, target)
			&& !secondaryNetwork.graph().containsEdge(source, target);
		Segment2D edge = new Segment2D(source, target);
		TestCanvas.canvas.draw(
			edge,
			DrawingSegment2D.withColorThin(Color.blue)
		);
		secondaryNetwork.graph().addVertex(source);
		secondaryNetwork.graph().addVertex(target);
		secondaryNetwork.graph().addSegmentAsEdge(edge);
		fullNetwork.graph().addSegmentAsEdge(edge);
		fullNetwork.addNetworkPart(edge, fullNetwork);
		fullNetwork.addNetworkPart(edge, secondaryNetwork);
		assert !ShamosHoeyAlgorithm.areIntersected(fullNetwork.graph().edgeSet());
	}

	/**
	 * [Kelly figure 42]
	 * <p>
	 * Adds new node between two existing nodes, removing an existing segment between them and placing 2 new roads to
	 * segment network.
	 * <p>
	 * Edges are split in the following graphs:
	 * <ul>
	 * <li>{@link #fullNetwork}</li>
	 * <li>{@link NetworksProducer#splitOriginalMesh}</li>
	 * <li>{@link InnerForest#enclosingCycle}</li>
	 * <li>or one of {@link InnerForest#enclosedCycles}</li>
	 * </ul>
	 *
	 * @param segment
	 * 	A segment from {@link #fullNetwork} on which a node is being inserted.
	 * @param splitPoint
	 * 	A node on that segment where the node resides at which the segment is to be split in two.
	 */
	void splitEdge(Segment2D segment, Point2D splitPoint) {
		assert !segment.end.equals(splitPoint) && !segment.start.equals(splitPoint);
		assert fullNetwork.graph().containsEdge(segment);
		minimumDistanceAssert(segment, splitPoint);
		fullNetwork.splitEdge(new SplitSegment2D(segment, splitPoint));
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
		return splitOriginalMesh.containsVertex(node);
	}

	private double deviatedLength(double roadSegmentLength) {
		return roadSegmentLength - networkGenerationParameters.secondaryNetworkSegmentLengthDeviation / 2 + random.nextDouble() *
			networkGenerationParameters.secondaryNetworkSegmentLengthDeviation;
	}

	void addTwoMissingConnectionsToEnclosedCycle(OrientedCycle cycle) {
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
		tryPlacingRoad(
			cycle.deviatedAngleBisector(leastPoint, false)
		);
		tryPlacingRoad(
			cycle.deviatedAngleBisector(greatestPoint, false)
		);
	}

	void addMissingConnectionToEnclosedCycle(OrientedCycle cycle, Point2D connectionPoint) {
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
		tryPlacingRoad(
			cycle.deviatedAngleBisector(farthestPoint, false)
		);
	}
}
