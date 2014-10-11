package org.tendiwa.graphs.algorithms.jerrumSinclair;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.jgrapht.UndirectedGraph;

import java.util.Set;

/**
 * Index of vertices of a bipartite graph. Each vertex has a unique index >= 0. Each vertex in the second partition
 * has index greater than all the indices of vertices in the first partition.
 */
class BipartiteGraphVertexIndex<V, E> {
	/**
	 * All vertices in a matching refer to their partner vertices by index. If a vertex doesn't have a partner vertex,
	 * it will refer to a non-existing vertex with this number.
	 */
	public static final int UNMATCHED_VERTEX = -1;
	final UndirectedGraph<V, E> graph;
	private final int numberOfAllVertices;
	private final TObjectIntMap<V> vertexToIndex;
	private final Object[] vertices;

	BipartiteGraphVertexIndex(UndirectedGraph<V, E> graph, Set<V> partition1, int numberOfAllVertices) {
		this.graph = graph;
		this.numberOfAllVertices = numberOfAllVertices;
		this.vertexToIndex = new TObjectIntHashMap<>(numberOfAllVertices);
		this.vertices = new Object[numberOfAllVertices];
		enumerateVertices(partition1);
	}

	/**
	 * Returns index under which {@code vertex} resides in the adjacency vector of this bipartite graph.
	 *
	 * @param vertex
	 * 	A vertex of this bipartite graph.
	 * @return Index under which {@code vertex} resides in the adjacency vector of this bipartite graph.
	 */
	int indexOf(V vertex) {
		assert vertexToIndex.containsKey(vertex);
		return vertexToIndex.get(vertex);
	}

	/**
	 * Find a vertex by its index and returns it.
	 *
	 * @param index
	 * 	Index of a vertex.
	 * @return Vertex object under that index.
	 */
	V getVertex(int index) {
		assert index >= 0 && index < numberOfAllVertices;
		return (V) vertices[index];
	}

	/**
	 * Assign a number to each vertex, so vertices from {@code partition} have indices less than vertices from the
	 * other partition.
	 *
	 * @param partition
	 * 	One partition of vertices of this bipartite graph.
	 */
	void enumerateVertices(Set<V> partition) {
		int i = 0;
		for (V partitionVertex : partition) {
			vertexToIndex.put(partitionVertex, i);
			vertices[i] = partitionVertex;
			i++;
		}
		for (V graphVertex : graph.vertexSet()) {
			if (!vertexToIndex.containsKey(graphVertex)) {
				vertexToIndex.put(graphVertex, i);
				vertices[i] = graphVertex;
				i++;
			}
		}
		assert i == numberOfAllVertices;
	}

	/**
	 * Checks if there is an edge between vertices with the specified indices.
	 *
	 * @param startIndex
	 * 	An index.
	 * @param endIndex
	 * 	Another index, interchangeable with the {@code startIndex} (order is not important,
	 * 	as the underlying graph is undirected).
	 * @return true if there is an edge between vertices with the specified indices, false otherwise.
	 */
	boolean containsEdge(int startIndex, int endIndex) {
		return graph.containsEdge((V) vertices[startIndex], (V) vertices[endIndex]);
	}

	int getSourceIndex(E edge) {
		assert vertexToIndex.get(graph.getEdgeSource(edge)) != vertexToIndex.get(graph.getEdgeTarget(edge));
		return vertexToIndex.get(graph.getEdgeSource(edge));
	}

	int getTargetIndex(E edge) {
		assert vertexToIndex.get(graph.getEdgeSource(edge)) != vertexToIndex.get(graph.getEdgeTarget(edge));
		return vertexToIndex.get(graph.getEdgeTarget(edge));
	}

	V getVertexByIndex(int index) {
		return (V) vertices[index];
	}
}
