package org.tendiwa.geometry.smartMesh;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

public class SplitOriginalMesh implements NetworkPart {
	private final MutableGraph2D graph;

	SplitOriginalMesh(UndirectedGraph<Point2D, Segment2D> originalGraph) {
		this.graph = new MutableGraph2D();
		Graphs.addGraph(graph, originalGraph);
	}

	@Override
	public MutableGraph2D graph() {
		return graph;
	}

	OrientedCycle createCycleNetworkPart(MinimalCycle<Point2D, Segment2D> minimalCycle) {
		return new OrientedCycle(minimalCycle, graph);
	}
}