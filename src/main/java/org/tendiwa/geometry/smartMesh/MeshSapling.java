package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.graphs2d.Cycle2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;

import java.util.Random;

public final class MeshSapling extends BasicMutableGraph2D {

	private final CycleWithInnerCycles perforatedCycle;
	private final InnerNetwork innerNetwork;
	private boolean filled = false;

	MeshSapling(
		CycleEdges cycleEdges,
		CycleWithInnerCycles perforatedCycle,
		NetworkGenerationParameters config,
		Random random
	) {
		super(perforatedCycle);
		this.perforatedCycle = perforatedCycle;
	}

	void fill() {
		this.innerNetwork = new Flood(
			perforatedCycle,
			config,
			random
		).createNetwork();
		fullGraph.integrateForest(innerNetwork);
	}


	Graph2D network() {
		return innerNetwork.fullGraph().without(perforatedCycle);
	}

	Cycle2D cycle() {
		return outerCycle.graph();
	}
}