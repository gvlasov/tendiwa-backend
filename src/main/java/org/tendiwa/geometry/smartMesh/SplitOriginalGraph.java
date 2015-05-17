package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;

public final class SplitOriginalGraph extends BasicMutableGraph2D implements SharingSubgraph2D {
	private final Graph2D originalGraph;
	private final FullGraph supergraph;

	@Override
	public FullGraph supergraph() {
		return supergraph;
	}

	SplitOriginalGraph(
		Graph2D originalGraph,
		FullGraph supergraph
	) {
		super(originalGraph);
		this.originalGraph = originalGraph;
		this.supergraph = supergraph;
	}
}