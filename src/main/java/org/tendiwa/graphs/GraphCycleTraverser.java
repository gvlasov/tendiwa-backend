package org.tendiwa.graphs;

import org.jgrapht.UndirectedGraph;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;

public final class GraphCycleTraverser {
	public static <V, E> Step1<V, E> traverse(UndirectedGraph<V, E> graph) {
		return new Step1<>(graph);
	}

	public static class Step1<V, E> {

		private final UndirectedGraph<V, E> graph;

		private Step1(UndirectedGraph<V, E> graph) {
			this.graph = graph;
		}

		public Step2<V, E> startingWith(V startVertex) {
			return new Step2<>(graph, startVertex);
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
			GraphCycleTraverser.traverse(graph, startVertex, consumer);
		}
	}

	private static <V, E> void traverse(UndirectedGraph<V, E> graph, V startVertex, BiConsumer<V, V> consumer) {
		V previousVertex = null;
		V currentVertex = startVertex;
		V nextVertex;
		do {
			Set<E> edges = graph.edgesOf(currentVertex);
			assert edges.size() == 2;
			Iterator<E> iter = edges.iterator();
			E anyNeighborEdge = iter.next();
			nextVertex = graph.getEdgeSource(anyNeighborEdge);
			if (nextVertex == currentVertex) {
				nextVertex = graph.getEdgeTarget(anyNeighborEdge);
			}
			if (nextVertex == previousVertex) {
				anyNeighborEdge = iter.next();
				nextVertex = graph.getEdgeSource(anyNeighborEdge);
				if (nextVertex == currentVertex) {
					nextVertex = graph.getEdgeTarget(anyNeighborEdge);
				}
				assert nextVertex != previousVertex;
			}
			assert nextVertex != currentVertex;
			assert nextVertex != previousVertex;
			assert graph.containsEdge(currentVertex, nextVertex);
			consumer.accept(currentVertex, nextVertex);
			previousVertex = currentVertex;
			currentVertex = nextVertex;
		} while (nextVertex != startVertex);
	}
}
