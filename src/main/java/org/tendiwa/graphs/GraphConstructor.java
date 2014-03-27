package org.tendiwa.graphs;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * Reduces boilerplate code while contructing specific graphs from test cases and demonstrations.
 */
public class GraphConstructor<V> {
    private final BiMap<Integer, V> vertices = HashBiMap.create();
    private final SimpleGraph<V, DefaultEdge> graph;

    public GraphConstructor() {
        graph = new SimpleGraph<>(new EdgeFactory<V, DefaultEdge>() {
            @Override
            public DefaultEdge createEdge(V v, V v2) {
                return new DefaultEdge();
            }
        });
    }

    public GraphConstructor<V> vertex(int alias, V vertex) {
        if (vertices.containsKey(alias)) {
            throw new IllegalArgumentException("Alias " + alias + " is already taken");
        }
        vertices.put(alias, vertex);
        graph.addVertex(vertex);
        return this;
    }

    public GraphConstructor<V> edge(int start, int end) {
        graph.addEdge(vertices.get(start), vertices.get(end));
        return this;
    }

    public SimpleGraph<V, DefaultEdge> graph() {
        return graph;
    }

    /**
     * Returns alias of a vertex.
     *
     * @param vertex
     *         A vertex to get alias of.
     * @return Character or null of vertex was not added to this GraphConstructor.
     */
    public int aliasOf(V vertex) {
        return vertices.inverse().get(vertex);
    }
}
