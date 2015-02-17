package org.tendiwa.geometry.smartMesh;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.graphs.GraphChainTraversal;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

final class Forest {
	private final FullNetwork fullNetwork;
	private final SegmentInserter segmentInserter;
	private final OrientedCycle enclosingCycle;
	private final NetworkGenerationParameters parameters;
	private final Random random;
	private Canopy canopy;

	Forest(
		FullNetwork fullNetwork,
		SegmentInserter segmentInserter,
		OrientedCycle enclosingCycle,
		NetworkGenerationParameters parameters,
		Random random
	) {
		this.fullNetwork = fullNetwork;
		this.segmentInserter = segmentInserter;
		this.enclosingCycle = enclosingCycle;
		this.parameters = parameters;
		this.random = random;

		this.canopy = new Canopy();
	}


	private void depleteTrees(Collection<FloodNetworkTree> floodTrees) {
		while (!floodTrees.isEmpty()) {
			Iterator<FloodNetworkTree> iterator = floodTrees.iterator();
			while (iterator.hasNext()) {
				FloodNetworkTree tree = iterator.next();
				if (tree.isDepleted()) {
					iterator.remove();
				} else {
					tree.propagate();
				}
			}
		}
	}

	private Ray createFloodStart(Point2D point) {
		assert segmentInserter.isDeadEnd(point);
		return enclosingCycle.deviatedAngleBisector(point, true);
	}

	private FloodNetworkTree createFloodTree(Ray ray) {
		return new FloodNetworkTree(
			ray,
			canopy,
			fullNetwork.graph(),
			segmentInserter,
			parameters,
			random
		);
	}

	/**
	 * Starts flood-filling innards of the cycle from no greater than {@code maxNumOfStartPoints} points.
	 */
	void grow() {
		Collection<Point2D> startingPoints = new CycleWithStartingPoints(
			fullNetwork,
			parameters
		).snapAndInsertStartingPoints(
			computeRootsOnSegments()
		);
		TestCanvas.canvas.drawAll(startingPoints, DrawingPoint2D.withColorAndSize(Color.red, 5));
		Set<FloodNetworkTree> floodTrees = startingPoints.stream()
			.map(this::createFloodStart)
			.map(this::createFloodTree)
			.collect(Collectors.toCollection(LinkedHashSet::new));
		depleteTrees(floodTrees);
	}

	private Map<Segment2D, List<Point2D>> computeRootsOnSegments() {
		List<Point2D> vertexList = GraphChainTraversal
			.traverse(enclosingCycle.graph())
			.startingWith(enclosingCycle.graph().vertexSet().stream().findAny().get())
			.stream()
			.map(GraphChainTraversal.NeighborsTriplet::current)
			.collect(Collectors.toList());
		return IntervalsAlongPolygonBorder.compute(
			vertexList,
			parameters.segmentLength,
			parameters.secondaryNetworkSegmentLengthDeviation,
			enclosingCycle.graph()::getEdge,
			random
		);
	}
}
