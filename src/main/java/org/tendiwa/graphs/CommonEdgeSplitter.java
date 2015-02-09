package org.tendiwa.graphs;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;

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
			edgesToGraphs
				.computeIfAbsent(edge, (e) -> new ArrayList<>())
				.add(graph);
		}
	}

	public void splitEdge(E edge, V edgeSource, V edgeTarget, V splitVertex) {
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
	}
}
