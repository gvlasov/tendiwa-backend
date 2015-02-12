package org.tendiwa.graphs;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.collections.IterableToStream;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * In a 2-regular graph, applies a {@link java.util.function.BiConsumer} to all possible successive pairs of
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
public final class GraphCycleTraversal {
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

		public Step3<V, E> awayFrom(V preStartVertex) {
			return new Step3<>(graph, startVertex, preStartVertex);
		}

		public Step4<V, E> until(Predicate<NeighborsTriplet<V>> endPostCondition) {
			return new Step4<>(graph, startVertex, null, endPostCondition);
		}

		public Stream<NeighborsTriplet<V>> stream() {
			return traverse(graph, startVertex, null, x -> false);
		}

	}

	public static class Step3<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final V startVertex;
		private final V preStartVertex;

		public Step3(UndirectedGraph<V, E> graph, V startVertex, V preStartVertex) {
			this.graph = graph;
			this.startVertex = startVertex;
			this.preStartVertex = preStartVertex;
		}

		/**
		 * This method exists because of this: <a href="http://stackoverflow
		 * .com/questions/20746429/java-8-limit-infinite-stream-by-a-predicate">(StackOverflow) Java 8: Limit infinite
		 * stream by a predicate</a>
		 */
		public Step4<V, E> until(Predicate<NeighborsTriplet<V>> endPostCondition) {
			return new Step4<>(graph, startVertex, preStartVertex, endPostCondition);
		}

		public Stream<NeighborsTriplet<V>> stream() {
			return traverse(graph, startVertex, preStartVertex, x -> false);
		}
	}

	public static class Step4<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final V startVertex;
		private final V preStartVertex;
		private final Predicate<NeighborsTriplet<V>> endPostCondition;

		public Step4(UndirectedGraph<V, E> graph, V startVertex, V preStartVertex, Predicate<NeighborsTriplet<V>> endPostCondition) {
			this.graph = graph;
			this.startVertex = startVertex;
			this.preStartVertex = preStartVertex;
			this.endPostCondition = endPostCondition;
		}

		public Stream<NeighborsTriplet<V>> stream() {
			return traverse(graph, startVertex, preStartVertex, endPostCondition);
		}
	}

	private static <V, E> Stream<NeighborsTriplet<V>> traverse(
		UndirectedGraph<V, E> twoRegularGraph,
		V startVertex,
		@Nullable V preStartVertex,
		Predicate<NeighborsTriplet<V>> endPostCondition
	) {
		Iterator<NeighborsTriplet<V>> iterator = new Iterator<NeighborsTriplet<V>>() {
			V currentVertex = startVertex;
			V nextVertex;
			V previousVertex = preStartVertex;

			@Override
			public boolean hasNext() {
				return nextVertex != startVertex;
			}

			@Override
			public NeighborsTriplet<V> next() {
				Set<E> edges = twoRegularGraph.edgesOf(currentVertex);
				if (edges.size() != 2) {
					throw new IllegalArgumentException("Graph is not 2-regular");
				}
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
				NeighborsTriplet<V> next = new NeighborsTriplet<>(previousVertex, currentVertex, nextVertex);
				previousVertex = currentVertex;
				currentVertex = nextVertex;
				if (endPostCondition.test(next)) {
					nextVertex = startVertex; // So next hasNext() will return false
				}
				return next;
			}
		};

		return IterableToStream.stream(iterator);
	}

	public static class NeighborsTriplet<V> {
		private final V previousVertex;
		private final V currentVertex;
		private final V nextVertex;

		public NeighborsTriplet(V previousVertex, V currentVertex, V nextVertex) {
			this.previousVertex = previousVertex;
			this.currentVertex = currentVertex;
			this.nextVertex = nextVertex;
		}

		public V previous() {
			return previousVertex;
		}

		public V current() {
			return currentVertex;
		}

		public V next() {
			return nextVertex;
		}
	}
}
