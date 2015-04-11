package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.algorithms.SameOrPerpendicularSlopeGraphEdgesPerturbations;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.tendiwa.collections.Collectors.toLinkedHashSet;

public final class OriginalMeshCell {
	private final Forest forest;
	private final OrientedCycle outerCycle;

	OriginalMeshCell(
		FullNetwork fullNetwork,
		SplitOriginalMesh splitOriginalMesh,
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		Collection<MinimalCycle<Point2D, Segment2D>> innerMinimalCycles,
		NetworkGenerationParameters config,
		Random random
	) {
		this.outerCycle = splitOriginalMesh.createCycleNetworkPart(originalMinimalCycle);
		fullNetwork.addNetworkPart(outerCycle);
		Set<OrientedCycle> innerCycles = innerMinimalCycles.stream()
			.map(splitOriginalMesh::createCycleNetworkPart)
			.collect(toLinkedHashSet());
		innerCycles.forEach(fullNetwork::addNetworkPart);
		this.forest = new Forester(
			outerCycle,
			innerCycles,
			config,
			random
		).createForest();
	}

	public UndirectedGraph<Point2D, Segment2D> network() {
		return forest.fullGraph().without(outerCycle.graph());
	}

	public UndirectedGraph<Point2D, Segment2D> cycle() {
		return outerCycle.graph();
	}


	public List<SecondaryRoadNetworkBlock> enclosedBlocks() {
		UndirectedGraph<Point2D, Segment2D> fullCellGraph = PlanarGraphs.copyGraph(forest.fullGraph());
		SameOrPerpendicularSlopeGraphEdgesPerturbations.perturb(fullCellGraph, 1e-4);
		return PlanarGraphs
			.minimumCycleBasis(fullCellGraph)
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new SecondaryRoadNetworkBlock(cycle.vertexList()))
			.collect(toList());
	}

	ImmutableSet<Segment2D> innerTreesEndSegments() {
		return forest.deadEndsOnCycles();
	}

	public Set<Point2D> exitsOnCycles() {
		return forest.whereBranchesStuckIntoCycles();
	}
}