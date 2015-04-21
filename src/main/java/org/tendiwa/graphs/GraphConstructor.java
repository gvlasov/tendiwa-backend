package org.tendiwa.graphs;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.collections.SuccessiveTuples;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Reduces boilerplate code while constructing specific undirected graphs for test cases and demonstrations.
 * <p>
 * <pre>
 * {@code
 * GraphConstructor<Point, Line> gc = new GraphConstructor<>((p1, p2)->new Line(p1, p2))
 *      .vertex(0, new Point(10, 10))
 *      .vertex(1, new Point(10, 20))
 *      .vertex(2, new Point(20, 10))
 *      .vertex(3, new Point(20, 20)).withEdgesTo(0, 1, 2) // places edges from 3 to 0, to 1 and to 2.
 *
 *      .vertex(4, new Point(30, 30))
 *      .edge(4, 5) // Edges to and from not yet existent aliases can be added.
 *      .vertex(5, new Point(40, 40))
 *
 *      .vertex(6, new Point(50, 30))
 *      .vertex(7, new Point(60, 30))
 *      .vertex(8, new Point(60, 40))
 *      .vertex(9, new Point(50, 40))
 *      .cycle(6,7,8,9)
 *
 *      .path(2, 4, 6);
 *
 *      System.out.println(gc.aliasOf(new Point(50, 30)); // 6
 *
 *      UndirectedGraph<Point, Line> graph = gc.graph();
 * }
 * </pre>
 */
public class GraphConstructor<V, E> {
	private final BiMap<Integer, V> vertices = HashBiMap.create();
	private int lastVertexAlias = Integer.MIN_VALUE;
	private final SimpleGraph<V, E> graph;
	private final LinkedList<int[]> edges = new LinkedList<>();
	private boolean finished = false;

	/**
	 * Creates a GraphConstructor that builds a graph with {@code DefaultEdge} edges.
	 *
	 * @param <V>
	 * 	Vertex type.
	 * @return A new GraphConstructor.
	 */
	public static <V> GraphConstructor<V, DefaultEdge> createDefault() {
		return new GraphConstructor<>((v, v2) -> new DefaultEdge());
	}

	public GraphConstructor(EdgeFactory<V, E> factory) {
		graph = new SimpleGraph<>(factory);
	}

	/**
	 * Adds a vertex to the graph.
	 *
	 * @param alias
	 * 	An arbitrary numerical name for a vertex.
	 * @param vertex
	 * 	The vertex.
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
	 * Adds an edge between two vertices under given aliases. Note that you <i>can</i> add edges between aliases that
	 * have not yet been assignet to a vertex.
	 *
	 * @param start
	 * 	A vertex
	 * @param end
	 * 	Another vertex (the order is irrelevant since the graph produces is undirected).
	 * @return The same GraphConstructor to chain methods.
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@code start == end}.
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

	public GraphConstructor<V, E> cycleOfVertices(List<V> vertices) {
		// TODO: Are edges added as well?
		int firstAlias = peekNextAlias();
		for (V v : vertices) {
			vertex(getNextAlias(), v);
		}
		cycle(IntStream.range(firstAlias, peekNextAlias()).toArray());
		return this;
	}

	private int getNextAlias() {
		lastVertexAlias = peekNextAlias();
		return lastVertexAlias;
	}

	private int peekNextAlias() {
		return lastVertexAlias == Integer.MIN_VALUE ? 0 : lastVertexAlias + 1;
	}

	/**
	 * Adds edges between the last vertex added to the graph and the given vertices.
	 *
	 * @param vertices
	 * 	Vertices to add edges from last vertex.
	 * @return The same GraphConstructor to chain methods.
	 */
	public GraphConstructor<V, E> withEdgesTo(int... vertices) {
		if (lastVertexAlias == Integer.MIN_VALUE) {
			throw new IllegalStateException("No vertices added yet");
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
	 * 	A sequence of vertices.
	 * @return The same GraphConstructor to chain methods.
	 */
	public GraphConstructor<V, E> path(int... vertices) {
		for (int i = 1; i < vertices.length; i++) {
			edge(vertices[i - 1], vertices[i]);
		}
		return this;
	}

	/**
	 * Creates a chain of edges between a sequence of vertices, and then loops it by also connecting the last vertex
	 * and
	 * the first vertex with an edge.
	 *
	 * @param vertices
	 * 	A sequence of vertices.
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
			V sourceVertex = vertices.get(edge[0]);
			V targetVertex = vertices.get(edge[1]);
			if (sourceVertex == null) {
				throw new IllegalStateException("No vertex was assigned to alias " + edge[0]);
			}
			if (targetVertex == null) {
				throw new IllegalStateException("No vertex was assigned to alias " + edge[1]);
			}
			graph.addEdge(sourceVertex, targetVertex);
		}
		finished = true;
		return graph;
	}

	/**
	 * Returns an alias of a vertex.
	 *
	 * @param vertex
	 * 	A vertex to get alias of.
	 * @return An integer, or null if this alias has not yet been associated with a vertex.
	 */
	public int aliasOf(V vertex) {
		return vertices.inverse().get(vertex);
	}

	public GraphConstructor<V, E> chain(List<V> points) {
		vertex(getNextAlias(), points.get(0));
		SuccessiveTuples.forEach(points, (a, b) -> {
			int currentAlias = lastVertexAlias;
			int nextAlias = getNextAlias();
			vertex(nextAlias, b);
			edge(currentAlias, nextAlias);
		});
		return this;
	}
}
