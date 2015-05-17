package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.graphs2d.Cycle2D_Wr;
import org.tendiwa.geometry.graphs2d.Mesh2D;
import org.tendiwa.geometry.graphs2d.PerforatedCycle2D;
import org.tendiwa.graphs.algorithms.SameOrPerpendicularSlopeGraphEdgesPerturbations;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.tendiwa.collections.Collectors.toImmutableSet;

public final class IncrementingSubgraph extends BasicMutableGraph2D implements SharingSubgraph2D {

	private final CycleWithInnerCycles perforatedCycle;
	private final InnerNetwork innerNetwork;

	IncrementingSubgraph(
		FullGraph fullGraph,
		CycleWithInnerCycles perforatedCycle,
		NetworkGenerationParameters config,
		Random random
	) {
		super(perforatedCycle);
		this.perforatedCycle = perforatedCycle;
		fullGraph.registerSubgraph(perforatedCycle.hull());
		this.innerNetwork = new Flood(
			perforatedCycle,
			config,
			random
		).createNetwork();
		fullGraph.integrateForest(innerNetwork);
	}


	UndirectedGraph<Point2D, Segment2D> network() {
		return innerNetwork.fullGraph().without(perforatedCycle);
	}

	UndirectedGraph<Point2D, Segment2D> cycle() {
		return outerCycle.graph();
	}


	public List<Polygon> enclosedBlocks() {
		UndirectedGraph<Point2D, Segment2D> fullCellGraph = PlanarGraphs.copyGraph(innerNetwork.fullGraph());
		SameOrPerpendicularSlopeGraphEdgesPerturbations.perturb(fullCellGraph, 1e-4);
		return PlanarGraphs
			.minimumCycleBasis(fullCellGraph)
			.minimalCyclesSet()
			.stream()
			.map(cycle ->
					new BasicPolygon(
						cycle.vertexList()
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