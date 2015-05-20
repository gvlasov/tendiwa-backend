package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.Mesh2D;
import org.tendiwa.geometry.graphs2d.PolylineGraph2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.awt.Color;
import java.util.Random;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public final class SmartMeshedNetwork extends MutableGraph2D implements MeshedNetwork {

	SmartMeshedNetwork(
		Graph2D originalGraph,
		NetworkGenerationParameters parameters,
		Random random
	) {
		assert originalGraph.isPlanar();
		MeshedNetworkPartitioning partitioning = new MeshedNetworkPartitioning(originalGraph);
		CycleEdges cycleEdges = new CycleEdges(partitioning.cycles());
		ImmutableSet<Flood> floods = partitioning
			.nestedCycles()
			.stream()
			.map(perforatedCycle ->
					new Flood(
						perforatedCycle,
						cycleEdges,
						this,
						parameters,
						random
					)
			)
			.collect(toImmutableSet());


		floods.forEach(MeshSapling::fill);

		TestCanvas.canvas.draw(
			new DrawableGraph2D.Thin(
				getFullCycleGraph(),
				Color.red
			)
		);
	}

	public ImmutableSet<Segment2D> innerTreeSegmentsEnds() {
		return networks.stream()
			.flatMap(network -> network.innerTreesEndSegments().stream())
			.collect(toImmutableSet());
	}
}
