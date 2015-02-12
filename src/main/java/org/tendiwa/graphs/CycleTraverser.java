package org.tendiwa.graphs;

import org.jgrapht.UndirectedGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public final class CycleTraverser<V, E> {
	// TODO: Is this a duplicate of GraphCycleTraverser?


	private final UndirectedGraph<V, E> graph;
	private final Comparator<V> comparator;
	private final Set<V> cycleVertices;
	private final BiConsumer<? super V, ? super V> consumer;

	public static <V, E> Step1<V, E> forGraph(UndirectedGraph<V, E> graph) {
		return new Step1<>(graph);
	}


	public static final class Step1<V, E> {
		private final UndirectedGraph<V, E> graph;

		private Step1(UndirectedGraph<V, E> graph) {
			this.graph = graph;
		}

		public Step2<V, E> sortNeighborsWith(Comparator<V> comparator) {
			return new Step2<>(graph, comparator);
		}

	}

	public static final class Step2<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final Comparator<V> comparator;

		private Step2(UndirectedGraph<V, E> graph, Comparator<V> comparator) {
			this.graph = graph;
			this.comparator = comparator;
		}

		public Step3<V, E> withCycleVertices(Set<V> cycleVertices) {
			return new Step3<>(graph, comparator, cycleVertices);
		}

	}

	public static final class Step3<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final Comparator<V> comparator;
		private final Set<V> cycleVertices;

		private Step3(UndirectedGraph<V, E> graph, Comparator<V> comparator, Set<V> cycleVertices) {
			this.graph = graph;
			this.comparator = comparator;
			this.cycleVertices = cycleVertices;
		}

		public void forEach(BiConsumer<? super V, ? super V> consumer) {
			new CycleTraverser<>(graph, comparator, cycleVertices, consumer).act();
		}
	}


	private CycleTraverser(
		UndirectedGraph<V, E> graph,
		Comparator<V> comparator,
		Set<V> cycleVertices,
		BiConsumer<? super V, ? super V> consumer
	) {
		this.graph = graph;
		this.comparator = comparator;
		this.cycleVertices = cycleVertices;
		this.consumer = consumer;
	}

	private void act() {
		V startVertex, currentVertex, nextVertex;
		startVertex = currentVertex = findVertexOfDegreeGt2();
		nextVertex = getInitialDirectionVertex(currentVertex);
		while (nextVertex != startVertex) {
			consumer.accept(currentVertex, nextVertex);
			V previousVertex = currentVertex;
			currentVertex = nextVertex;
			nextVertex = getNextVertex(previousVertex, currentVertex);
			assert currentVertex != nextVertex;
		}
	}

	/**
	 * Searches among {@link #cycleVertices} for any vertex with degree > 2.
	 *
	 * @return A vertex of graph's cycle.
	 */
	private V findVertexOfDegreeGt2() {
		for (V vertex : cycleVertices) {
			if (graph.degreeOf(vertex) > 2) {
				return vertex;
			}
		}
		throw new RuntimeException("Could not find a vertex of degree > 2");
	}

	private V getNextVertex(V previousVertex, V currentVertex) {
		for (E edge : graph.edgesOf(currentVertex)) {
			if (canBeNextVertex(graph.getEdgeSource(edge), previousVertex, currentVertex)) {
				return graph.getEdgeSource(edge);
			}
			if (canBeNextVertex(graph.getEdgeTarget(edge), previousVertex, currentVertex)) {
				return graph.getEdgeTarget(edge);
			}
		}
		throw new RuntimeException("Could not find next vertex");
	}

	private boolean canBeNextVertex(V candidateVertex, V currentVertex, V previousVertex) {
		return candidateVertex != previousVertex
			&& candidateVertex != currentVertex
			&& cycleVertices.contains(candidateVertex);
	}

	/**
	 * Deterministically finds a neighbor of {@code startVertex} in {@link #graph} that is a vertex of
	 * network's cycle.
	 *
	 * @param startVertex
	 * 	A vertex on graph's cycle.
	 * @return A neighbor vertex of {@code startVertex} that is on graph's cycle too.
	 */
	private V getInitialDirectionVertex(V startVertex) {
		Set<E> neighborEdges = graph.edgesOf(startVertex);
		List<V> neighborVertices = new ArrayList<>(neighborEdges.size() + 1);
		neighborVertices.add(startVertex);
		neighborEdges.forEach(edge -> {
			neighborVertices.add(graph.getEdgeSource(edge));
			neighborVertices.add(graph.getEdgeTarget(edge));
		});
		neighborVertices.sort(comparator);
		for (V vertex : neighborVertices) {
			if (cycleVertices.contains(vertex)) {
				return vertex;
			}
		}
		throw new RuntimeException("Could not find next vertex");
	}
}
