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
public final class MinimalCycle<V, E> implements Primitive<V>, Iterable<E> {
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
			System.out.println(previous + " " + next);
			answer.addVertex(next);
			answer.addEdge(previous, next, graph.getEdge(previous, next));
			previous = next;
		}
		answer.addEdge(previous, cycle.get(0));
		return answer;
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

	@Override
	/**
	 * Iterates over all edges of this minimal cycle.
	 */
	public Iterator<E> iterator() {
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
