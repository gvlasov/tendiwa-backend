package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

final class FullRoadGraph {
	private UndirectedGraph<Point2D, Segment2D> fullRoadGraph;
	private UndirectedGraph<Point2D, Segment2D> actualCyclesRoadGraph;

	public FullRoadGraph(
		UndirectedGraph<Point2D, Segment2D> lowLevelRoadGraph,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges,
		ImmutableSet<NetworkWithinCycle> networks
	) {
		compute(lowLevelRoadGraph, holderOfSplitCycleEdges, networks);
	}

	void compute(
		UndirectedGraph<Point2D, Segment2D> lowLevelRoadGraph,
		HolderOfSplitCycleEdges holderOfSplitCycleEdges,
		ImmutableSet<NetworkWithinCycle> networks
	) {
		UndirectedGraph<Point2D, Segment2D> union = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
		lowLevelRoadGraph.vertexSet().forEach(union::addVertex);
		Set<Point2D> verticesOfCyclesSubgraph = new HashSet<>();
		Set<Segment2D> edgesOfCyclesSubgraph = new HashSet<>();
		for (Segment2D edge : lowLevelRoadGraph.edgeSet()) {
			if (holderOfSplitCycleEdges.isEdgeSplit(edge)) {
				UndirectedGraph<Point2D, Segment2D> graph = holderOfSplitCycleEdges.getGraph(edge);
				graph.vertexSet().forEach(union::addVertex);
				graph.vertexSet().forEach(verticesOfCyclesSubgraph::add);
				for (Segment2D subEdge : graph.edgeSet()) {
					boolean added = union.addEdge(subEdge.start, subEdge.end, subEdge);
					assert added;
					edgesOfCyclesSubgraph.add(subEdge);
				}
			} else {
				union.addEdge(edge.start, edge.end, edge);
				verticesOfCyclesSubgraph.add(edge.start);
				verticesOfCyclesSubgraph.add(edge.end);
				edgesOfCyclesSubgraph.add(edge);
			}
		}
		for (NetworkWithinCycle cell : networks) {
			cell.network()
				.vertexSet()
				.stream()
					// TODO: Do we need this filter?
				.filter(vertex -> !union.containsVertex(vertex))
				.forEach(union::addVertex);
			cell.network()
				.edgeSet()
				.stream()
					// TODO: Do we need this filter?
				.filter(edge -> !union.containsEdge(edge))
				.forEach(edge -> union.addEdge(edge.start, edge.end, edge));
		}
		fullRoadGraph = union;
		actualCyclesRoadGraph = new UndirectedSubgraph<>(fullRoadGraph, verticesOfCyclesSubgraph, edgesOfCyclesSubgraph);
	}

	UndirectedGraph<Point2D, Segment2D> getFullRoadGraph() {
		return fullRoadGraph;
	}

	/**
	 * Returns a graph of roads that form actual cycles. Actual cycles are made of edges and vertices of original
	 * cycles and also split edges remembered in {@link org.tendiwa.settlements.networks.RoadsPlanarGraphModel#holderOfSplitCycleEdges}.
	 *
	 * @return Actual road cycles graph.
	 * @see org.tendiwa.settlements.networks.RoadsPlanarGraphModel#originalRoadGraph for original cycles.
	 */
	UndirectedGraph<Point2D, Segment2D> getCyclesRoadGraph() {
		return actualCyclesRoadGraph;
	}
}
