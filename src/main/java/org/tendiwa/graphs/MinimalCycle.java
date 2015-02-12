package org.tendiwa.graphs;

import com.google.common.collect.ImmutableList;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A cycle of vertices, ordered clockwise.
 *
 * @param <V>
 * 	Type of vertices.
 * @param <E>
 * 	Type of edges.
 */
public final class MinimalCycle<V, E> implements Primitive<V> {
	private final List<V> cycle = new ArrayList<>();
	private UndirectedGraph<V, E> graph;

	/**
	 * @param graph
	 * 	A larger graph in which this minimal cycle exists.
	 * @param cycle
	 * 	A list of cells of this minimal cycle.
	 */
	MinimalCycle(UndirectedGraph<V, E> graph, List<V> cycle) {
		assert cycle.size() > 2;
		this.graph = graph;
		this.cycle.addAll(cycle);
	}

	public UndirectedGraph<V, E> asGraph() {
		UndirectedGraph<V, E> answer = new SimpleGraph<>(graph.getEdgeFactory());
		Iterator<V> iterator = cycle.iterator();
		V previous = iterator.next();
		answer.addVertex(previous);
		while (iterator.hasNext()) {
			V next = iterator.next();
			answer.addVertex(next);
			answer.addEdge(previous, next, graph.getEdge(previous, next));
			previous = next;
		}
		answer.addEdge(previous, cycle.get(0));
		return answer;
	}

	/**
	 * Returns the number of edges or vertices in this cycle, which is the same number by definition of a cycle.
	 *
	 * @return Number of edges or vertices in this cycle.
	 */
	public int size() {
		return cycle.size();
	}

	@Override
	@Deprecated
	/**
	 * Inserting is disabled for this class.
	 * @throws java.lang.UnsupportedOperationException
	 */
	public void insert(V vertex) {
		throw new UnsupportedOperationException();
	}

	public Iterable<E> asEdges() {
		return () -> {
			int numberOfEdges = cycle.size();
			List<E> edgesOfCycle = new ArrayList<>(numberOfEdges);
			for (int i = 0; i < numberOfEdges; i++) {
				boolean isLastEdge = i == numberOfEdges - 1;
				E edge = graph.getEdge(cycle.get(i), cycle.get(isLastEdge ? 0 : i + 1));
				if (edge == null) {
					edge = graph.getEdge(cycle.get(isLastEdge ? 0 : i + 1), cycle.get(i));
				}
				assert edge != null;
				assert graph.edgeSet().contains(edge);
				edgesOfCycle.add(edge);
			}
			return edgesOfCycle.iterator();
		};
	}

	public Iterable<V> asVertices() {
		return new Iterable<V>() {
			@Override
			public Iterator<V> iterator() {
				Iterator<V> iterator = cycle.iterator();
				return new Iterator<V>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public V next() {
						return iterator.next();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException(
							"Can't remove vertices from " + this.getClass().getName()
						);
					}
				};
			}
		};
	}


	/**
	 * Returns all vertices of a cycle. Order is guaranteed to be clockwise.
	 *
	 * @return A {@link List} of all vertices of this cycle.
	 */
	public List<V> vertexList() {
		return ImmutableList.copyOf(cycle);
	}
}
