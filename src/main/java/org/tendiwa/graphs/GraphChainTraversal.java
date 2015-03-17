package org.tendiwa.graphs;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.collections.IterableToStream;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Applies a {@link java.util.function.BiConsumer} to all possible successive pairs of vertices that form consecutive
 * segments, until this chain comes to its beginning or a vertex with degree > 2 is met.
 * <p>
 * A graph is traversed in such an order that if a vertex is in consumer's {@code next} argument,
 * the next time it will be in consumer's {@code current} argument.
 * <p>
 * Starting from a particular vertex, there may be two paths that can be taken, since each vertex has exactly two
 * neighbors. If {@link Step2#awayFrom(Object)} and {@link Step3#past(Object)} are not set, it is not defined which of
 * these paths will be taken.
 */
public final class GraphChainTraversal {
	public static <V, E> Step1<V, E> traverse(UndirectedGraph<V, E> graph) {
		return new Step1<>(graph);
	}

	public static final class Step1<V, E> {

		private final UndirectedGraph<V, E> graph;

		private Step1(UndirectedGraph<V, E> graph) {
			this.graph = graph;
		}

		public Step2<V, E> startingWith(V startVertex) {
			vertexMustBeInGraph(startVertex, graph);
			return new Step2<>(graph, Objects.requireNonNull(startVertex));
		}

	}

	private static <V, E> void vertexMustBeInGraph(V vertex, UndirectedGraph<V, E> graph) {
		if (!graph.containsVertex(vertex)) {
			String methodName = new Exception().getStackTrace()[2].getMethodName();
			throw new IllegalArgumentException(
				"Vertex set with method \"" + methodName + "\" must be in the traversed graph"
			);
		}
	}

	private static <V, E> void verticesMustBeNeighbors(V vertex, V startVertex, UndirectedGraph<V, E> graph) {
		if (!graph.containsEdge(startVertex, vertex)) {
			String methodName = new Exception().getStackTrace()[2].getMethodName();
			throw new IllegalArgumentException(
				"Vertex set with method \"" + methodName
					+ "\" must be a neighbor of startVertex in the traversed graph"
			);
		}
	}

	public static final class Step2<V, E> {

		private final UndirectedGraph<V, E> graph;
		private final V startVertex;

		private Step2(UndirectedGraph<V, E> graph, V startVertex) {
			this.graph = graph;
			this.startVertex = startVertex;
		}

		public Step3<V, E> awayFrom(V preStartVertex) {
			vertexMustBeInGraph(preStartVertex, graph);
			verticesMustBeNeighbors(preStartVertex, startVertex, graph);
			return new Step3<>(graph, startVertex, preStartVertex);
		}

		public Step4<V, E> past(V nextVertex) {
			vertexMustBeInGraph(nextVertex, graph);
			verticesMustBeNeighbors(nextVertex, startVertex, graph);
			return new Step4<>(graph, startVertex, null, nextVertex);
		}

		public Step5<V, E> until(Predicate<NeighborsTriplet<V>> endPostCondition) {
			if (graph.degreeOf(startVertex) > 2) {
				throw new IllegalStateException(
					"Can't decide which way to go from start vertex without knowing previous and next vertices " +
						"because start vertex has degree > 2 (it is " + graph.degreeOf(startVertex) + ")"
				);
			}
			return new Step5<>(graph, startVertex, null, autoChooseVertexToGoPast(), endPostCondition);
		}

		private V autoChooseVertexToGoPast() {
			E startEdge = graph.edgesOf(startVertex).iterator().next();
			if (graph.getEdgeTarget(startEdge).equals(startVertex)) {
				return graph.getEdgeSource(startEdge);
			} else {
				assert graph.getEdgeSource(startEdge).equals(startVertex);
				return graph.getEdgeTarget(startEdge);
			}

		}

		public Stream<NeighborsTriplet<V>> stream() {
			return traverse(graph, startVertex, null, autoChooseVertexToGoPast(), x -> false);
		}

	}

	public static final class Step3<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final V startVertex;
		private final V preStartVertex;

		public Step3(UndirectedGraph<V, E> graph, V startVertex, V preStartVertex) {
			this.graph = graph;
			this.startVertex = startVertex;
			this.preStartVertex = preStartVertex;
		}

		public Step4<V, E> past(V nextVertex) {
			vertexMustBeInGraph(nextVertex, graph);
			verticesMustBeNeighbors(nextVertex, startVertex, graph);
			return new Step4<>(graph, startVertex, preStartVertex, nextVertex);
		}

		public Stream<NeighborsTriplet<V>> stream() {
			return traverse(graph, startVertex, preStartVertex, null, x -> false);
		}

	}

	public static final class Step4<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final V startVertex;
		private final V preStartVertex;
		private final V nextVertex;

		public Step4(UndirectedGraph<V, E> graph, V startVertex, V preStartVertex, V nextVertex) {
			this.graph = graph;
			this.startVertex = startVertex;
			this.preStartVertex = preStartVertex;
			this.nextVertex = nextVertex;
		}

		/**
		 * This method exists because of this: <a href="http://stackoverflow
		 * .com/questions/20746429/java-8-limit-infinite-stream-by-a-predicate">(StackOverflow) Java 8: Limit infinite
		 * stream by a predicate</a>
		 */
		public Step5<V, E> until(Predicate<NeighborsTriplet<V>> endPostCondition) {
			return new Step5<>(graph, startVertex, preStartVertex, nextVertex, endPostCondition);
		}

		public Stream<NeighborsTriplet<V>> stream() {
			return traverse(graph, startVertex, preStartVertex, nextVertex, x -> false);
		}
	}

	public static final class Step5<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final V startVertex;
		private final V preStartVertex;
		private final V nextVertex;
		private final Predicate<NeighborsTriplet<V>> endPostCondition;

		public Step5(
			UndirectedGraph<V, E> graph,
			V startVertex,
			V preStartVertex,
			V nextVertex,
			Predicate<NeighborsTriplet<V>> endPostCondition
		) {
			this.graph = graph;
			this.startVertex = startVertex;
			this.preStartVertex = preStartVertex;
			this.nextVertex = nextVertex;
			this.endPostCondition = endPostCondition;
		}

		public Stream<NeighborsTriplet<V>> stream() {
			return traverse(graph, startVertex, preStartVertex, nextVertex, endPostCondition);
		}
	}

	private static <V, E> Stream<NeighborsTriplet<V>> traverse(
		UndirectedGraph<V, E> graph,
		V startVertex,
		@Nullable V preStartVertex,
		@Nullable V vertexToGoPast,
		Predicate<NeighborsTriplet<V>> endPostCondition
	) {
		return IterableToStream.stream(new GraphChainIterator<>(
			graph,
			startVertex,
			preStartVertex,
			vertexToGoPast,
			endPostCondition
		));
	}

	private final static class GraphChainIterator<V, E> implements Iterator<NeighborsTriplet<V>> {
		private final UndirectedGraph<V, E> graph;
		private V currentVertex;
		private V nextVertex;
		private V previousVertex;
		private final V startVertex;
		private final Predicate<NeighborsTriplet<V>> endPostCondition;

		GraphChainIterator(
			UndirectedGraph<V, E> graph,
			V startVertex,
			V preStartVertex,
			V vertexToGoPast,
			Predicate<NeighborsTriplet<V>> endPostCondition
		) {
			this.graph = graph;
			this.currentVertex = startVertex;
			this.nextVertex = vertexToGoPast;
			this.previousVertex = preStartVertex;
			this.startVertex = startVertex;
			this.endPostCondition = endPostCondition;
		}

		@Override
		public boolean hasNext() {
			return nextVertex != startVertex;
		}

		@Override
		public NeighborsTriplet<V> next() {
			if (currentVertex != startVertex || nextVertex == null) {
				chooseNextVertex();
				assert nextVertex != currentVertex;
				assert nextVertex != previousVertex;
				assert nextVertex == null || graph.containsEdge(currentVertex, nextVertex);
			}
			NeighborsTriplet<V> triplet = new NeighborsTriplet<>(previousVertex, currentVertex, nextVertex);
			previousVertex = currentVertex;
			currentVertex = nextVertex;
			if (endPostCondition.test(triplet) || nextVertex == null) {
				nextVertex = startVertex;
			}
			return triplet;
		}

		private void chooseNextVertex() {
			Set<E> edges = graph.edgesOf(currentVertex);
			if (edges.size() > 2) {
//					throw new IllegalStateException(
//						"Came to a vertex that has " + edges.size() + " edges; " +
//							"vertices during traversal must have 2 or 1 edges"
//					);
				nextVertex = null;
			} else {
				Iterator<E> iter = edges.iterator();
				E anyNeighborEdge = iter.next();
				nextVertex = graph.getEdgeSource(anyNeighborEdge);
				if (nextVertex.equals(currentVertex)) {
					nextVertex = graph.getEdgeTarget(anyNeighborEdge);
				}
				if (nextVertex.equals(previousVertex)) {
					if (iter.hasNext()) {
						anyNeighborEdge = iter.next();
						nextVertex = graph.getEdgeSource(anyNeighborEdge);
						if (nextVertex.equals(currentVertex)) {
							nextVertex = graph.getEdgeTarget(anyNeighborEdge);
						}
						assert nextVertex != previousVertex;
					} else {
						nextVertex = null;
					}
				}
				assert nextVertex == null || !nextVertex.equals(previousVertex)
					: nextVertex + " " + previousVertex + " " + (nextVertex == previousVertex);
			}
		}
	}

	public static final class NeighborsTriplet<V> {
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

		@Override
		public String toString() {
			return "NeighborsTriplet{" +
				"previousVertex=" + previousVertex +
				", currentVertex=" + currentVertex +
				", nextVertex=" + nextVertex +
				'}';
		}
	}
}
