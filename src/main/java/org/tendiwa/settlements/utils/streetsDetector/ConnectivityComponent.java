package org.tendiwa.settlements.utils.streetsDetector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.tendiwa.collections.Collectors;

import java.util.Set;

final class ConnectivityComponent<V, E> {

	private final UndirectedGraph<V, E> fullGraph;
	private final Set<V> vertices;

	ConnectivityComponent(
		UndirectedGraph<V, E> fullGraph,
		Set<V> componentVertices
	) {

		this.fullGraph = fullGraph;
		this.vertices = componentVertices;
	}

	UndirectedGraph<V, E> graph() {
		return new UndirectedSubgraph<>(
			fullGraph,
			vertices,
			findEdgesOfComponent()
		);
	}

	private Set<E> findEdgesOfComponent(
	) {
		return fullGraph.edgeSet().stream()
			.filter(this::edgeEndsAreInComponent)
			.collect(Collectors.toImmutableSet());
	}

	private boolean edgeEndsAreInComponent(E edge) {
		return vertices.contains(fullGraph.getEdgeSource(edge))
			&& vertices.contains(fullGraph.getEdgeTarget(edge));
	}
}
