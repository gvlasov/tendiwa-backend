package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.PerforatedCycle2D;
import org.tendiwa.geometry.graphs2d.PolylineGraph2D;

import java.awt.Color;
import java.util.Random;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public final class SmartMeshedNetwork implements MeshedNetwork {
	private final FullGraph fullGraph;
	private final SplitOriginalGraph splitOriginalGraph;
	private final Graph2D originalGraph;
	private ImmutableSet<PerforatedCycle2D> meshes;

	SmartMeshedNetwork(
		Graph2D originalGraph,
		NetworkGenerationParameters parameters,
		Random random
	) {
		assert originalGraph.isPlanar();
		this.originalGraph = originalGraph;
		MeshedNetworkPartitioning partitioning = new MeshedNetworkPartitioning(originalGraph);
		FullGraph fullGraph = new FullGraph(originalGraph);
		SharingSubgraph2D splitOriginalGraph = new SplitOriginalGraph(
			originalGraph,
			fullGraph
		);
		ImmutableSet<IncrementingSubgraph> meshes = partitioning
			.cycles()
			.stream()
			.map(cycle ->
					new CycleWithInnerCycles(
						cycle,
						partitioning.cycles()
					)
			)
			.map(
				perforatedCycle ->
					new IncrementingSubgraph(
						fullGraph,
						perforatedCycle,
						parameters,
						random
					)
			)
			.collect(toImmutableSet());
		meshes.forEach(fullGraph::registerSubgraph);
		if (meshes.isEmpty()) {
			throw new GeometryException(
				"A RoadPlanarGraphModel with 0 city networks was made"
			);
		}
		TestCanvas.canvas.draw(
			new DrawableGraph2D.Thin(
				getFullCycleGraph(),
				Color.red
			)
		);
	}


	@Override
	public ImmutableSet<PolylineGraph2D> filaments() {
		return splitOriginalGraph.filaments();
	}

	@Override
	public ImmutableSet<PerforatedCycle2D> meshes() {
		return meshes;
	}

	@Override
	public Graph2D fullGraph() {
		return fullGraph;
	}

	@Override
	public Graph2D outerHull() {
		return new OuterHull(this).graph();
	}

	public Graph2D getFullCycleGraph() {
		return fullCycleGraph;
	}

	public ImmutableSet<Segment2D> innerTreeSegmentsEnds() {
		return networks.stream()
			.flatMap(network -> network.innerTreesEndSegments().stream())
			.collect(toImmutableSet());
	}
}
