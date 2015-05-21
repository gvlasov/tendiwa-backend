package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;

import java.util.Random;

import static org.tendiwa.collections.Collectors.toImmutableSet;

final class SmartMeshedNetwork extends BasicMutableGraph2D implements MeshedNetwork {

	SmartMeshedNetwork(
		Graph2D originalGraph,
		NetworkGenerationParameters parameters,
		Random random
	) {
		super();
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
		floods.forEach(Flood::fill);
	}

	@Override
	public ImmutableSet<Polygon> meshCells() {
		throw new UnsupportedOperationException();
	}
}
