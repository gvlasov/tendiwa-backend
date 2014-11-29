package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.MinimalCycle;

/**
 * Holds a lazily initialized graph of edges that form the cycle this network is enclosed in.
 */
final class CycleGraph {
	private final MinimalCycle<Point2D, Segment2D> originalMinimalCycle;
	private final HolderOfSplitCycleEdges holderOfSplitCycleEdges;
	private UndirectedGraph<Point2D, Segment2D> graph;

	public CycleGraph(
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges
	) {
		this.originalMinimalCycle = originalMinimalCycle;
		this.holderOfSplitCycleEdges = holderOfSplitCycleEdges;
	}

	/**
	 * Constructs the enclosing cycle of this network from {@link #originalMinimalCycle} and {@link
	 * #holderOfSplitCycleEdges}.
	 */
	private void init() {
		assert graph == null;
		graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
		for (Segment2D edge : originalMinimalCycle) {
			graph.addVertex(edge.start);
			graph.addVertex(edge.end);
			if (holderOfSplitCycleEdges.isEdgeSplit(edge)) {
				UndirectedGraph<Point2D, Segment2D> splitGraph = holderOfSplitCycleEdges.getGraph(edge);
				splitGraph.vertexSet().forEach(graph::addVertex);
				for (Segment2D splitEdge : splitGraph.edgeSet()) {
					graph.addEdge(splitEdge.start, splitEdge.end, splitEdge);
				}
			} else {
				graph.addEdge(edge.start, edge.end, edge);
			}
		}
	}

	public UndirectedGraph<Point2D, Segment2D> getGraph() {
		if (graph == null) {
			init();
		}
		return graph;
	}
}
