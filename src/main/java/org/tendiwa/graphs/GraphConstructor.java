package org.tendiwa.graphs;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.LinkedList;

/**
 * Reduces boilerplate code while constructing specific graphs from test cases and demonstrations.
 * <p>
 */
public class GraphConstructor<V, E> {
    private final BiMap<Integer, V> vertices = HashBiMap.create();
    private int lastVertexAlias = Integer.MIN_VALUE;
    private final SimpleGraph<V, E> graph;
    private final LinkedList<int[]> edges = new LinkedList<>();
    private boolean finished = false;

    public static <V> GraphConstructor<V, DefaultEdge> create() {
        return new GraphConstructor<>(new EdgeFactory<V, DefaultEdge>() {
            @Override
            public DefaultEdge createEdge(V v, V v2) {
                return new DefaultEdge();
            }
        });
    }

    public GraphConstructor(EdgeFactory<V, E> factory) {
        graph = new SimpleGraph<>(factory);
    }

    /**
     * Adds a vertex to the graph.
     *
     * @param alias
     *         An arbitrary numerical name for a vertex.
     * @param vertex
     *         The vertex.
     * @return The same GraphConstructor to chain methods.
     */
    public GraphConstructor<V, E> vertex(int alias, V vertex) {
        if (vertices.containsKey(alias)) {
            throw new IllegalArgumentException("Alias " + alias + " is already taken");
        }
        vertices.put(alias, vertex);
        graph.addVertex(vertex);
        lastVertexAlias = alias;
        return this;
    }

    /**
     * Adds an edge between two vertices under given aliases. Note that you <i>can</i> add edges between aliases that have not yet been assignet to a vertex.
     *
     * @param start
     *         A vertex
     * @param end
     *         Another vertex (the order is irrelevant since the graph produces is undirected).
     * @return The same GraphConstructor to chain methods.
     * @throws java.lang.IllegalArgumentException
     *         if {@code start == end}.
     */
    public GraphConstructor<V, E> edge(int start, int end) {
        if (start == Integer.MIN_VALUE || end == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("You can't use Integer.MIN_VALUE as an edge alias");
        }
        if (start == end) {
            throw new IllegalArgumentException("Can't add an edge from a vertex to itself");
        }
        edges.add(new int[]{start, end});
        return this;
    }

    /**
     * Adds edges between the last vertex added to the graph and the given vertices.
     *
     * @param vertices
     *         Vertices to add edges from last vertex.
     * @return The same GraphConstructor to chain methods.
     */
    public GraphConstructor<V, E> withEdges(int... vertices) {
        if (lastVertexAlias == Integer.MIN_VALUE) {
            throw new IllegalStateException("You can call method edges only after at least one vertex was added");
        }
        for (int vertex : vertices) {
            edge(lastVertexAlias, vertex);
        }
        return this;
    }

    /**
     * Creates a chain of edges between a sequence of vertices.
     *
     * @param vertices
     *         A sequence of vertices.
     * @return The same GraphConstructor to chain methods.
     */
    public GraphConstructor<V, E> path(int... vertices) {
        for (int i = 1; i < vertices.length; i++) {
            edge(vertices[i - 1], vertices[i]);
        }
        return this;
    }

    /**
     * Creates a chain of edges between a sequence of vertices, and then loops it by also connecting the last vertex and the first vertex with an edge.
     *
     * @param vertices
     *         A sequence of vertices.
     * @return The same GraphConstructor to chain methods.
     */
    public GraphConstructor<V, E> cycle(int... vertices) {
        for (int i = 1; i < vertices.length; i++) {
            edge(vertices[i - 1], vertices[i]);
        }
        edge(vertices[vertices.length - 1], vertices[0]);
        return this;
    }

    /**
     * Returns the built graph.
     *
     * @return A new graph. Only one graph can be produced with one builder.
     */
    public SimpleGraph<V, E> graph() {
        if (finished) {
            throw new IllegalStateException("A graph has already been produced by this GraphConstructor");
        }
        for (int[] edge : edges) {
            graph.addEdge(vertices.get(edge[0]), vertices.get(edge[1]));
        }
        finished = true;
        return graph;
    }

    /**
     * Returns an alias of a vertex.
     *
     * @param vertex
     *         A vertex to get alias of.
     * @return An integer, or null if this alias has not yet been associated with a vertex.
     */
    public int aliasOf(V vertex) {
        return vertices.inverse().get(vertex);
    }
}
