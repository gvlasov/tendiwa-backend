package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.BasicPolygon;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.algorithms.SameOrPerpendicularSlopeGraphEdgesPerturbations;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.tendiwa.collections.Collectors.toImmutableSet;

public final class OriginalMeshCell {
	private final InnerNetwork innerNetwork;
	private final OrientedCycle outerCycle;

	OriginalMeshCell(
		FullNetwork fullNetwork,
		OrientedCycle outerCycle,
		Set<OrientedCycle> innerCycles,
		NetworkGenerationParameters config,
		Random random
	) {
		this.outerCycle = outerCycle;
		fullNetwork.addNetworkPart(outerCycle);
		this.innerNetwork = new Flood(
			outerCycle,
			innerCycles,
			config,
			random
		).createNetwork();
		fullNetwork.integrateForest(innerNetwork);
	}

	public UndirectedGraph<Point2D, Segment2D> network() {
		return innerNetwork.fullGraph().without(outerCycle.graph());
	}

	public UndirectedGraph<Point2D, Segment2D> cycle() {
		return outerCycle.graph();
	}


	public List<SecondaryRoadNetworkBlock> enclosedBlocks() {
		UndirectedGraph<Point2D, Segment2D> fullCellGraph = PlanarGraphs.copyGraph(innerNetwork.fullGraph());
		SameOrPerpendicularSlopeGraphEdgesPerturbations.perturb(fullCellGraph, 1e-4);
		return PlanarGraphs
			.minimumCycleBasis(fullCellGraph)
			.minimalCyclesSet()
			.stream()
			.map(cycle ->
					new SecondaryRoadNetworkBlock(
						new BasicPolygon(
							cycle.vertexList()
						)
					)
			)
			.collect(toList());
	}

	ImmutableSet<Segment2D> innerTreesEndSegments() {
		return innerNetwork.removableSegments();
	}

	public Set<Point2D> exitsOnCycles() {
		return innerNetwork
			.whereBranchesStuckIntoCycles()
			.flatMap(CutSegment2D::pointStream)
			.collect(toImmutableSet());
	}
}