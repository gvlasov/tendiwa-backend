package org.tendiwa.graphs.algorithms.jerrumSinclair;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.NeighborIndex;
import org.jgrapht.graph.SimpleGraph;

import java.util.Set;

class Matching<V, E> {
	private static final int UNMATCHED_SEARCH_INITIAL_VALUE = -1;
	private final BipartiteGraphVertexIndex<V, E> vertexIndex;
	private final NeighborIndex<V, E> neighbors;
	private final int numberOfAllVertices;
	private final int numberOfVerticesInPartition;
	private final Set<E> perfectMatching;
	int[] adjacency;
	private boolean isPerfect = true;
	/**
	 * At the beginning, no indices are unmapped
	 */
	int unmappedIndex1 = -1;
	int unmappedIndex2 = -1;

	Matching(
		BipartiteGraphVertexIndex<V, E> vertexIndex,
		int numberOfAllVertices,
		int numberOfVerticesInPartition,
		Set<E> initialPerfectMatching
	) {
		this.numberOfAllVertices = numberOfAllVertices;
		this.numberOfVerticesInPartition = numberOfVerticesInPartition;
		this.perfectMatching = initialPerfectMatching;
		this.vertexIndex = vertexIndex;
		this.neighbors = new NeighborIndex<>(vertexIndex.graph);
		adjacency = new int[numberOfAllVertices];
		initAdjacencyVector();
	}

	public boolean isPerfect() {
		return isPerfect;
	}

	void initAdjacencyVector() {
		for (E edge : perfectMatching) {
			int sourceIndex = vertexIndex.getSourceIndex(edge);
			int targetIndex = vertexIndex.getTargetIndex(edge);
			assert sourceIndex != targetIndex;
			assert adjacency[sourceIndex] == 0;
			adjacency[sourceIndex] = targetIndex;
			assert adjacency[targetIndex] == 0;
			adjacency[targetIndex] = sourceIndex;
		}
	}


	/**
	 * Removes edge that starts at index {@code part1Index}.
	 *
	 * @param part1Index
	 * 	Index of an edge in this bipartite graph.
	 */
	void unmapEdge(int part1Index) {
		assert part1Index < numberOfVerticesInPartition && part1Index >= 0;
		assert adjacency[adjacency[part1Index]] != BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		assert adjacency[part1Index] != BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		unmappedIndex1 = part1Index;
		unmappedIndex2 = adjacency[part1Index];
		adjacency[adjacency[part1Index]] = BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		adjacency[part1Index] = BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		isPerfect = false;
	}

	/**
	 * Checks if there is an edge coming from a vertex with index {@code part1Index}.
	 *
	 * @param part1Index
	 * 	Index of an edge in this bipartite graph.
	 * @return true if there is an edge coming from a vertex with index {@code part1Index}, false otherwise.
	 */
	boolean containsEdge(int part1Index) {
		return adjacency[part1Index] != BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
	}

	/**
	 * Adds an edge between two vertices of this
	 *
	 * @param part1Index
	 * 	Index of a vertex in the first partition of this bipartite graph.
	 * @param part2Index
	 * 	Index of a vertex in the second partition of this bipartite graph.
	 */
	void addEdge(int part1Index, int part2Index) {
		assert part1Index >= 0 && part1Index < numberOfVerticesInPartition;
		assert part2Index >= numberOfVerticesInPartition && part2Index < numberOfAllVertices;
		assert adjacency[part1Index] == BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		assert adjacency[part2Index] == BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		adjacency[part1Index] = part2Index;
		adjacency[part2Index] = part1Index;
		if (part1Index == unmappedIndex1 || part2Index == unmappedIndex1) {
			unmappedIndex1 = -1;
		}
		if (part1Index == unmappedIndex2 || part2Index == unmappedIndex2) {
			unmappedIndex2 = -1;
		}
		isPerfect = true;
	}

	void loadAnotherAdjacencyVector(RememberedMatching rememberedMatching) {
		System.arraycopy(rememberedMatching.getAdjacencyArray(), 0, adjacency, 0, adjacency.length);
	}

	/**
	 * Creates a {@link org.jgrapht.UndirectedGraph} from this bipartite graph.
	 *
	 * @return A new graph.
	 */
	UndirectedGraph<V, E> createGraph() {
		SimpleGraph<V, E> answer = new SimpleGraph<>(vertexIndex.graph.getEdgeFactory());
		for (V vertex : vertexIndex.graph.vertexSet()) {
			answer.addVertex(vertex);
		}
		for (int i = 0; i < numberOfVerticesInPartition; i++) {
			assert adjacency[i] != BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
			answer.addEdge(
				vertexIndex.getVertexByIndex(i),
				vertexIndex.getVertexByIndex(adjacency[i])
			);
		}
		return answer;
	}

	/**
	 * Copies and modifies {@link RememberedMatching}'s adjacency array so that it contains the closest perfect
	 * matching. Calling this method assumes that adjacency array of {@link org.tendiwa.graphs.algorithms.jerrumSinclair.RememberedMatching}
	 * currently contains a near-perfect matching.
	 */
	public void restoreNearPerfectMatchingToPerfect(RememberedMatching nearPerfectMatching) {
		assert !nearPerfectMatching.isPerfect();
		assert adjacency.length == nearPerfectMatching.getAdjacencyArray().length;
		System.arraycopy(nearPerfectMatching.getAdjacencyArray(), 0, adjacency, 0, adjacency.length);
		boolean part1UnmatchedFound = false;

		// Initial values don't matter, they are guaranteed to be changed.
		int part1Unmatched = UNMATCHED_SEARCH_INITIAL_VALUE,
			part2Unmatched = UNMATCHED_SEARCH_INITIAL_VALUE;

		for (int i = 0; i < numberOfAllVertices; i++) {
			if (adjacency[i] == BipartiteGraphVertexIndex.UNMATCHED_VERTEX) {
				if (part1UnmatchedFound) {
					part2Unmatched = i;
				} else {
					part1Unmatched = i;
					part1UnmatchedFound = true;
				}
			}
		}
		assert part1UnmatchedFound;
		assert part1Unmatched != UNMATCHED_SEARCH_INITIAL_VALUE;
		assert part2Unmatched != UNMATCHED_SEARCH_INITIAL_VALUE;
		assert part1Unmatched >= 0 && part1Unmatched < numberOfVerticesInPartition;
		assert part2Unmatched >= numberOfVerticesInPartition && part2Unmatched < numberOfAllVertices;
		unmappedIndex1 = part1Unmatched;
		unmappedIndex2 = part2Unmatched;
		if (vertexIndex.containsEdge(part1Unmatched, part2Unmatched)) {
			addEdge(part1Unmatched, part2Unmatched);
		} else {
			// Canonical path (iii) from [Jerrum, Sinclair 1989]
			int index = findEdgeToRestoreGoodMatchingToPerfect();
			assert index != -1;
			int oppositeIndex = adjacency[index];
			assert oppositeIndex != -1;
			if (index < numberOfVerticesInPartition) {
				adjacency[index] = part2Unmatched;
				adjacency[part2Unmatched] = index;
				adjacency[oppositeIndex] = part1Unmatched;
				adjacency[part1Unmatched] = oppositeIndex;
			} else {
				adjacency[index] = part1Unmatched;
				adjacency[part1Unmatched] = index;
				adjacency[oppositeIndex] = part2Unmatched;
				adjacency[part2Unmatched] = oppositeIndex;
			}
		}
		unmappedIndex1 = -1;
		unmappedIndex2 = -1;
	}

	public boolean lacksOneEdgeToPerfectness() {
		V v = vertexIndex.getVertex(unmappedIndex1);
		V u = vertexIndex.getVertex(unmappedIndex2);
		return vertexIndex.graph.containsEdge(v, u);
	}

	/**
	 * Finds edge (u', v') from [Jerrum, Sinclair 1989, p. 9].
	 * <p>
	 * Finding such edge checks if this matching can be restored to a perfect matching in two edge
	 * operations, assuming matching is currently near-perfect. "Restored in two edge operations"
	 * means that there is a <i>canonical path (iii)</i> ([Jerrum, Sinclair 1989, p. 9]) between current matching and
	 * some perfect matching.
	 *
	 * @return Index of one of two vertices that belong to an edge that connects a neighbor of {@link #unmappedIndex1}
	 * and a neighbor of {@link #unmappedIndex2}, or -1 if the closes perfect matching couldn't be found.
	 * @see <a href="http://www.cs.berkeley.edu/~sinclair/perm.pdf">Approximating the permanent</a> for more on
	 * canonical paths.
	 */
	int findEdgeToRestoreGoodMatchingToPerfect() {
		V v = vertexIndex.getVertex(unmappedIndex1);
		V u = vertexIndex.getVertex(unmappedIndex2);
		Set<V> vNeighbors = neighbors.neighborsOf(v);
		Set<V> uNeighbors = neighbors.neighborsOf(u);
		// Neighbors of vertices in one partition are all in another partition, and vice versa. That's why variable
		// names are chosen so that u1 iterates over vNeighbors, not v1 over vNeighbors.
		for (V u1 : vNeighbors) {
			int vNeighborNeighborIndex = adjacency[vertexIndex.indexOf(u1)];
			for (V v1 : uNeighbors) {
				int uNeighborIndex = vertexIndex.indexOf(v1);
				if (vNeighborNeighborIndex == uNeighborIndex) {
					// Reachable in two edge changes
					assert vertexIndex.graph.containsEdge(v, u1);
					assert vertexIndex.graph.containsEdge(u, v1);
					return uNeighborIndex;
				}
			}
		}
		return -1;
	}

	/**
	 * Changes edge coming from vertex under index {@code startIndex} to end at a vertex under index {@code
	 * newEndIndex}.
	 *
	 * @param startIndex
	 * 	Start index of an existing edge.
	 * @param newEndIndex
	 * 	Index of an unmatched vertex.
	 */
	void moveEdgeEnd(int startIndex, int newEndIndex) {
		assert adjacency[newEndIndex] == BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		int oldEndIndex = adjacency[startIndex];
		assert oldEndIndex != BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		if (unmappedIndex1 == newEndIndex) {
			unmappedIndex1 = oldEndIndex;
		} else if (unmappedIndex2 == newEndIndex) {
			unmappedIndex2 = oldEndIndex;
		} else {
			assert false;
		}
		adjacency[oldEndIndex] = BipartiteGraphVertexIndex.UNMATCHED_VERTEX;
		adjacency[startIndex] = newEndIndex;
		adjacency[newEndIndex] = startIndex;
		isPerfect = false;
	}

	boolean isType0TargetTransition(
		int sourceIndex,
		int targetIndex
	) {
		return !isPerfect
			&& containsEdge(targetIndex)
			&& !containsEdge(sourceIndex);
	}

	boolean isType0SourceTransition(
		int sourceIndex,
		int targetIndex
	) {
		return !isPerfect()
			&& containsEdge(sourceIndex)
			&& !containsEdge(targetIndex);
	}

	boolean isType2Transition(
		int part1Index,
		int part2Index
	) {
		return !isPerfect()
			&& !containsEdge(part1Index)
			&& !containsEdge(part2Index);
	}

	boolean isType1Transition(int part1Index) {
		return isPerfect() && containsEdge(part1Index);
	}
}
