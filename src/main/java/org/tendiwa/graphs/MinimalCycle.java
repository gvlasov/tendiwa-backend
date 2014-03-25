package org.tendiwa.graphs;

import com.google.inject.internal.util.$SourceProvider;
import org.jgrapht.UndirectedGraph;

import java.util.*;

public class MinimalCycle<V, E> implements Primitive<V>, Iterable<E> {
    private final List<V> cycle = new ArrayList<>();
    private UndirectedGraph<V, E> graph;

    MinimalCycle(UndirectedGraph<V, E> graph, List<V> cycle) {
        this.graph = graph;
        this.cycle.addAll(cycle);
    }

    @Override
    public void insert(V vertex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        int numberOfEdges = cycle.size() - 1;
        List<E> edgesOfCycle = new ArrayList<>(numberOfEdges);
        for (int i = 0; i < numberOfEdges; i++) {
            E edge = graph.getEdge(cycle.get(i), cycle.get(i + 1));
            if (edge == null) {
                edge = graph.getEdge(cycle.get(i+1), cycle.get(i));
            }
            assert edge != null;
            edgesOfCycle.add(edge);
        }
        return edgesOfCycle.iterator();
    }
}
