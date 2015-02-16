package org.tendiwa.geometry.smartMesh;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.Graph2D;

final class CompleteMesh implements NetworkPart {
	private final Graph2D graph;

	CompleteMesh(UndirectedGraph<Point2D, Segment2D> originalGraph) {
		this.graph = new Graph2D();
		Graphs.addGraph(graph, originalGraph);
	}

	@Override
	public Graph2D graph() {
		return graph;
	}
}
