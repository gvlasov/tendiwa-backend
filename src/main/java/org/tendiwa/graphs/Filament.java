package org.tendiwa.graphs;

import com.google.common.collect.ImmutableList;
import org.jgrapht.UndirectedGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Filament<V, E> implements Primitive<V>, Iterable<E> {
	private final List<V> queue = new ArrayList<>();
	private UndirectedGraph<V, E> graph;

	Filament(UndirectedGraph<V, E> graph) {
		this.graph = graph;
	}

	@Override
	public void insert(V vertex) {
		queue.add(vertex);
	}

	@Override
	public Iterator<E> iterator() {
		int numberOfEdges = queue.size() - 1;
		List<E> edgesOfFilament = new ArrayList<>(numberOfEdges);
		for (int i = 0; i < numberOfEdges; i++) {
			E edge = graph.getEdge(queue.get(i), queue.get(i + 1));
			if (edge == null) {
				edge = graph.getEdge(queue.get(i + 1), queue.get(i));
			}
			assert edge != null;
			edgesOfFilament.add(edge);
		}
		return edgesOfFilament.iterator();
	}

	public List<V> vertexList() {
		return ImmutableList.<V>builder().addAll(queue).build();
	}
}
