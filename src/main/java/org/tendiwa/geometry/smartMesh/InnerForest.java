package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.graphs.GraphChainTraversal;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

final class InnerForest implements NetworkPart {
	private final FullNetwork fullNetwork;
	private final SegmentInserter segmentInserter;
	private final Collection<OrientedCycle> enclosedCycles;
	private final OrientedCycle enclosingCycle;
	private final NetworkGenerationParameters parameters;
	private final Random random;
	private final Canopy canopy;
	private final Set<FloodNetworkTree> floodTrees;
	private final Graph2D graph;

	InnerForest(
		FullNetwork fullNetwork,
		SplitOriginalMesh splitOriginalMesh,
		OrientedCycle enclosingCycle,
		Collection<OrientedCycle> enclosedCycles,
		NetworkGenerationParameters parameters,
		Random random
	) {
		this.fullNetwork = fullNetwork;
		this.enclosedCycles = enclosedCycles;
		this.enclosingCycle = enclosingCycle;
		this.parameters = parameters;
		this.random = random;

		this.graph = new Graph2D();
		this.segmentInserter = new SegmentInserter(
			fullNetwork,
			splitOriginalMesh.graph(),
			this,
			parameters,
			random
		);

		this.canopy = new Canopy();

		this.floodTrees = grow();
		addMissingConnectionsWithEnclosedCycles();
	}

	@Override
	public void integrate(CutSegment2D cutSegment) {
		NetworkPart.super.integrate(cutSegment);
		Segment2D original = cutSegment.originalSegment();
		if (canopy.hasLeafWithPetiole(original)) {
			Segment2D replacement = cutSegment.stream().skip(1).findFirst().get();
			canopy.replaceLeafWithPetiole(original, replacement);
		}
	}


	private void growTrees(Collection<FloodNetworkTree> floodTrees) {
		floodTrees = new ArrayList<>(floodTrees);
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
	private Set<FloodNetworkTree> grow() {
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
		growTrees(floodTrees);
		return floodTrees;
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

	ImmutableSet<Segment2D> leavesWithPetioles() {
		return floodTrees.stream()
			.flatMap(tree -> tree.leavesWithPetioles().stream())
			.collect(org.tendiwa.collections.Collectors.toImmutableSet());
	}

	public Graph2D graph() {
		return graph;
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
