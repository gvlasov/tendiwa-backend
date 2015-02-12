package org.tendiwa.graphs;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.tendiwa.geometry.Segment2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommonEdgeSplitter<V, E> {
	private final Map<E, Collection<Graph<V, E>>> edgesToGraphs = new LinkedHashMap<>();
	private final EdgeFactory<V, E> edgeFactory;

	public CommonEdgeSplitter(EdgeFactory<V, E> edgeFactory) {
		this.edgeFactory = edgeFactory;
	}

	public void addGraph(Graph<V, E> graph) {
		for (E edge : graph.edgeSet()) {
			addEdgeForGraph(graph, edge);
		}
	}

	public void addEdgeForGraph(Graph<V, E> graph, E edge) {
		assert graph.containsEdge(edge);
		edgesToGraphs
			.computeIfAbsent(edge, (e) -> new ArrayList<>())
			.add(graph);
	}

	public SplitEdgesPair<E> splitEdge(E edge, V edgeSource, V edgeTarget, V splitVertex) {
		E beforeSplit = edgeFactory.createEdge(edgeSource, splitVertex);
		E afterSplit = edgeFactory.createEdge(splitVertex, edgeTarget);
		for (Graph<V, E> graph : edgesToGraphs.get(edge)) {
			assert graph.getEdgeSource(edge).equals(edgeSource);
			assert graph.getEdgeTarget(edge).equals(edgeTarget);
			graph.removeEdge(edge);
			graph.addVertex(splitVertex);
			graph.addEdge(edgeSource, splitVertex, beforeSplit);
			graph.addEdge(splitVertex, edgeTarget, afterSplit);
		}
		return new SplitEdgesPair<>(beforeSplit, afterSplit);
	}

	public static class SplitEdgesPair<E> {
		public final E oneEdge;
		public final E anotherEdge;

		private SplitEdgesPair(E oneEdge, E anotherEdge) {
			this.oneEdge = oneEdge;
			this.anotherEdge = anotherEdge;
		}
	}
}
