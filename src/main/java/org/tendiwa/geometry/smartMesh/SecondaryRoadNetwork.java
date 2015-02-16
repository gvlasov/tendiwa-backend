package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.Sets;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.graphs.GraphCycleTraversal;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

final class SecondaryRoadNetwork implements NetworkPart {

	private final OrientedCycle enclosingCycle;
	private final FullNetwork fullNetwork;
	private final SegmentInserter segmentInserter;
	private final Graph2D graph;

	Set<DirectionFromPoint> filamentEnds;
	private final Collection<OrientedCycle> enclosedCycles;
	private final NetworkGenerationParameters networkGenerationParameters;
	private final Random random;

	SecondaryRoadNetwork(
		FullNetwork fullNetwork,
		NetworkPart splitOriginalMesh,
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
			splitOriginalMesh.graph(),
			this,
			networkGenerationParameters,
			random
		);

		floodFillFromStartingPoints();
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

	public Graph2D graph() {
		return graph;
	}


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
		Set<FloodNetworkTree> floodTrees = startingPoints.stream()
			.filter(point -> !floodFillingAlreadyCameTo(point))
			.map(this::createFloodStart)
			.map(this::createFloodTree)
			.collect(Collectors.toCollection(LinkedHashSet::new));
		depleteTrees(floodTrees);
	}

	private void depleteTrees(Set<FloodNetworkTree> floodTrees) {
		while (!floodTrees.isEmpty()) {
			Iterator<FloodNetworkTree> iterator = floodTrees.iterator();
			while (iterator.hasNext()) {
				FloodNetworkTree tree = iterator.next();
				tree.propagate();
				if (tree.isDepleted()) {
					iterator.remove();
				}
			}
		}
	}

	private DirectionFromPoint createFloodStart(Point2D point) {
		assert segmentInserter.isDeadEnd(point);
		return enclosingCycle.deviatedAngleBisector(point, true);
	}

	private FloodNetworkTree createFloodTree(DirectionFromPoint directionFromPoint) {
		return new FloodNetworkTree(
			directionFromPoint,
			segmentInserter,
			networkGenerationParameters,
			random
		);
	}

	private boolean floodFillingAlreadyCameTo(Point2D sourceNode) {
		boolean alreadyCame = graph.containsVertex(sourceNode);
		assert !alreadyCame || graph.degreeOf(sourceNode) == 1;
		return alreadyCame;
	}


	private void addMissingConnectionsWithEnclosedCycles() {
		Set<Point2D> secondaryNetworkVertices = graph.vertexSet();
		for (OrientedCycle cycle : enclosedCycles) {
			Set<Point2D> connections = Sets.intersection(
				cycle.graph().vertexSet(),
				secondaryNetworkVertices
			);
			if (connections.size() == 1) {
				segmentInserter.addMissingConnectionToEnclosedCycle(cycle, connections.iterator().next());
			} else if (connections.size() == 0) {
				segmentInserter.addTwoMissingConnectionsToEnclosedCycle(cycle);
			}
		}
	}
}
