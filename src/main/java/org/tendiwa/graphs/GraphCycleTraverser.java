package org.tendiwa.graphs;

import org.jgrapht.UndirectedGraph;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * In a 2-regular graph, applies a {@link java.util.function.BiConsumer} to all possible pairs of <i>neighbor</i>
 * vertices that form consecutive segments.
 * <p>
 * A graph is traversed in such an order that if a vertex is in consumer's {@code next} argument,
 * the next time it will be in consumer's {@code current} argument.
 * <p>
 * Starting from a particular vertex, there may be two paths that can be taken, since each vertex has exactly two
 * neighbors. It is not defined which of these paths will be taken.
 * <p>
 * For a 2-regular graph with multiple connectivity components, only one connectivity component will be traversed — the
 * one that contains the starting vertex.
 */
public final class GraphCycleTraverser {
	public static <V, E> Step1<V, E> traverse(UndirectedGraph<V, E> twoRegularGraph) {
		if (!twoRegularGraph.vertexSet().stream().allMatch(v -> twoRegularGraph.degreeOf(v) == 2)) {
			throw new IllegalArgumentException("Graph is not 2-regular");
		}
		return new Step1<>(twoRegularGraph);
	}

	public static class Step1<V, E> {

		private final UndirectedGraph<V, E> graph;

		private Step1(UndirectedGraph<V, E> graph) {
			this.graph = graph;
		}

		public Step2<V, E> startingWith(V startVertex) {
			return new Step2<>(graph, Objects.requireNonNull(startVertex));
		}

	}

	public static class Step2<V, E> {

		private final UndirectedGraph<V, E> graph;
		private final V startVertex;

		private Step2(UndirectedGraph<V, E> graph, V startVertex) {
			this.graph = graph;
			this.startVertex = startVertex;
		}

		public void forEachPair(BiConsumer<V, V> consumer) {
			GraphCycleTraverser.traverse(graph, startVertex, Objects.requireNonNull(consumer));
		}
	}

	private static <V, E> void traverse(UndirectedGraph<V, E> twoRegularGraph, V startVertex, BiConsumer<V, V> consumer) {
		V previousVertex = null;
		V currentVertex = startVertex;
		V nextVertex;
		do {
			Set<E> edges = twoRegularGraph.edgesOf(currentVertex);
			assert edges.size() == 2;
			Iterator<E> iter = edges.iterator();
			E anyNeighborEdge = iter.next();
			nextVertex = twoRegularGraph.getEdgeSource(anyNeighborEdge);
			if (nextVertex == currentVertex) {
				nextVertex = twoRegularGraph.getEdgeTarget(anyNeighborEdge);
			}
			if (nextVertex == previousVertex) {
				anyNeighborEdge = iter.next();
				nextVertex = twoRegularGraph.getEdgeSource(anyNeighborEdge);
				if (nextVertex == currentVertex) {
					nextVertex = twoRegularGraph.getEdgeTarget(anyNeighborEdge);
				}
				assert nextVertex != previousVertex;
			}
			assert nextVertex != currentVertex;
			assert nextVertex != previousVertex;
			assert twoRegularGraph.containsEdge(currentVertex, nextVertex);
			consumer.accept(currentVertex, nextVertex);
			previousVertex = currentVertex;
			currentVertex = nextVertex;
		} while (nextVertex != startVertex);
	}
}
