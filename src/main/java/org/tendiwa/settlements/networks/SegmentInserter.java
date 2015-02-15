package org.tendiwa.settlements.networks;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.awt.Color;
import java.util.Random;

/**
 * Inserts new roads into {@link org.tendiwa.settlements.networks.NetworksProducer#fullGraph} and its subgraphs.
 */
public class SegmentInserter {
	private final FullNetwork fullNetwork;
	private final Graph2D secondaryNetworkGraph;
	private final Graph2D splitOriginalMesh;
	private final NetworkGenerationParameters networkGenerationParameters;
	private final Random random;

	public SegmentInserter(
		FullNetwork fullNetwork,
		Graph2D splitOriginalGraph,
		Graph2D secondaryNetworkGraph,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {
		this.fullNetwork = fullNetwork;
		this.splitOriginalMesh = splitOriginalGraph;
		this.networkGenerationParameters = networkGenerationParameters;
		this.secondaryNetworkGraph = secondaryNetworkGraph;
		this.random = random;
	}


	/**
	 * [Kelly figure 42, function placeSegment]
	 * <p>
	 * Tries adding a new road to the secondary road network graph.
	 *
	 * @param source
	 * 	Start node of a new road.
	 * @param direction
	 * 	Angle of a road to x-axis, in radians.
	 * @return The new node, or null if placing did not succeed.
	 */
	SnapEvent tryPlacingRoad(Point2D source, double direction, boolean prohibitSnappingRightAway) {
		double roadLength = deviatedLength(networkGenerationParameters.segmentLength);
		Point2D unsnappedTargetNode = source.moveBy(
			roadLength * Math.cos(direction),
			roadLength * Math.sin(direction)
		);

		SnapEvent snapEvent = new SnapTest(
			networkGenerationParameters.snapSize,
			source,
			unsnappedTargetNode,
			fullNetwork.graph()
		).snap().integrateInto(fullNetwork, this);
		assert !source.equals(snapEvent.target());
		return snapEvent;
	}

	/**
	 * Creates an edge between two vertices and adds that edge to relevant graphs:
	 * <ul>
	 * <li>{@link #fullNetwork}</li>
	 * <li>{@link #secondaryNetworkGraph}</li>
	 * </ul>
	 *
	 * @param source
	 * 	Start of segment.
	 * @param target
	 * 	End of segment.
	 */
	void addSecondaryNetworkEdge(Point2D source, Point2D target) {
		Segment2D edge = new Segment2D(source, target);
		assert !fullNetwork.graph().containsEdge(edge)
			&& !secondaryNetworkGraph.containsEdge(edge);
		assert !ShamosHoeyAlgorithm.areIntersected(fullNetwork.graph().edgeSet());
		TestCanvas.canvas.draw(
			edge,
			DrawingSegment2D.withColorThin(Color.blue)
		);
		secondaryNetworkGraph.addVertex(source);
		secondaryNetworkGraph.addVertex(target);
		secondaryNetworkGraph.addSegmentAsEdge(edge);
		fullNetwork.graph().addSegmentAsEdge(edge);
		fullNetwork.addNetworkPart(edge, fullNetwork);
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
	 * <li>{@link org.tendiwa.settlements.networks.NetworksProducer#splitOriginalGraph}</li>
	 * <li>{@link org.tendiwa.settlements.networks.SecondaryRoadNetwork#enclosingCycle} or one of {@link
	 * org.tendiwa.settlements.networks
	 * .SecondaryRoadNetwork#enclosedCycles}</li>
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

	boolean chanceToConnect() {
		return random.nextDouble() < networkGenerationParameters.connectivity;
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
}
