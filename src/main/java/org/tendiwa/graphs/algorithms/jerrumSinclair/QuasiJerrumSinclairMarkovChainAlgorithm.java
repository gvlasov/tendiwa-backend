package org.tendiwa.graphs.algorithms.jerrumSinclair;

import org.jgrapht.UndirectedGraph;

import java.util.Random;
import java.util.Set;

public class QuasiJerrumSinclairMarkovChainAlgorithm<V, E> {
	private final BipartiteGraphVertexIndex<V, E> bipartiteGraphVertexIndex;
	private final int numberOfSteps;
	private final Random random;
	private final Matching<V, E> currentMatching;
	private final int numberOfAllVertices;

	/**
	 * Generates a perfect matching with distribution close to uniform from a set of all possible perfect matchings
	 * in a graph.
	 *
	 * @param graph
	 * 	A graph to generate perfect a matching in.
	 * @param partition1
	 * 	One of partitions of a bipartite graph to generate a perfect matching in.
	 * @param perfectMatching
	 * 	An already constructed perfect matching in {@code graph}. To
	 * 	construct this matching in the first place, you should use any maximum matching searching algorithm,
	 * 	for example, {@link org.jgrapht.alg.HopcroftKarpBipartiteMatching}.
	 * @param numberOfSteps
	 * 	How long to simulate Markov chain. The greater the number of steps,
	 * 	the closer distribution is to being uniform, however after certain number of steps gain from adding more
	 * 	steps approaches zero. I'm not sure how to determine the actual number of steps needed for probabilities for
	 * 	each outcome to fall into [(1+ε)^-1, 1+ε] with arbitrarily small ε.
	 * 	fall into
	 * @param random
	 * 	Edge type.
	 * @return A perfect matching with a number of edges equal to that in the initial state of {@code matchingToMutate}.
	 * The matching returned is the {@code matchingToMutate} instance, mutated.
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@code matchingToMutate} is not a valid maximum matching in
	 * 	{@code graph}.
	 * @see <a href="http://mathworld.wolfram.com/PerfectMatching.html">Perfect matching in graph theory</a>
	 */
	QuasiJerrumSinclairMarkovChainAlgorithm(
		UndirectedGraph<V, E> graph,
		Set<V> partition1,
		Set<E> perfectMatching,
		int numberOfSteps,
		Random random
	) {
		int numberOfVerticesInPartition = partition1.size();
		this.numberOfAllVertices = numberOfVerticesInPartition * 2;
		assert partition1.size() * 2 == graph.vertexSet().size() : partition1.size() + " " + graph.vertexSet().size();
		this.numberOfSteps = numberOfSteps;
		this.random = random;
		this.bipartiteGraphVertexIndex = new BipartiteGraphVertexIndex<>(
			graph,
			partition1,
			numberOfAllVertices
		);
		this.currentMatching = new Matching<>(
			bipartiteGraphVertexIndex,
			numberOfAllVertices,
			numberOfVerticesInPartition,
			perfectMatching
		);
		if (!isPerfectMatching(perfectMatching, partition1)) {
			throw new IllegalArgumentException("The matching provided is not a perfect matching in the graph provided");
		}
	}

	/**
	 * Checks if a subgraph of {@link #bipartiteGraphVertexIndex} is a perfect matching in a bipartite graph.
	 *
	 * @param matching
	 * 	Edges of a subgraph.
	 * @param partition
	 * 	One partition of vertices.
	 * @return true if {@code matching} is perfect (i.e., it defines a bijection between two partitions),
	 * false otherwise.
	 */
	public boolean isPerfectMatching(Set<E> matching, Set<V> partition) {
		assert partition.size() * 2 == bipartiteGraphVertexIndex.graph.vertexSet().size();
		if (matching.size() != partition.size()) {
			return false;
		}
		for (E edge : matching) {
			V source = bipartiteGraphVertexIndex.graph.getEdgeSource(edge);
			V target = bipartiteGraphVertexIndex.graph.getEdgeTarget(edge);
			if (!(partition.contains(source) ^ partition.contains(target))) {
				return false;
			}
		}
		return true;
	}

	UndirectedGraph<V, E> compute() {
		E[] edges = (E[]) bipartiteGraphVertexIndex.graph.edgeSet().toArray();

		RememberedMatching rememberedMatching = new RememberedMatching(numberOfAllVertices);
		rememberedMatching.updateWith(currentMatching);

		for (int i = 0; i < numberOfSteps; i++) {
			int edgeIndex = (int) Math.floor(edges.length * random.nextDouble());
			E edge = edges[edgeIndex];
			int part1Index = bipartiteGraphVertexIndex.getSourceIndex(edge);
			int part2Index = bipartiteGraphVertexIndex.getTargetIndex(edge);
			assert part1Index != part2Index;
			if (part1Index > part2Index) {
				int buf = part1Index;
				part1Index = part2Index;
				part2Index = buf;
			}
			if (currentMatching.isType1Transition(part1Index)) {
				// Remove one edge from a perfect matching
				rememberedMatching.addChange(currentMatching.adjacency[part1Index]);
				rememberedMatching.addChange(part1Index);
				currentMatching.unmapEdge(part1Index);
				rememberedMatching.updateWith(currentMatching);
			} else {
				if (currentMatching.isType2Transition(part1Index, part2Index)) {
					// Add one edge to a near-perfect matching so it becomes perfect
					rememberedMatching.addChange(part2Index);
					rememberedMatching.addChange(part1Index);
					currentMatching.addEdge(part1Index, part2Index);
					rememberedMatching.updateWith(currentMatching);
				} else if (currentMatching.isType0SourceTransition(part1Index, part2Index)) {
					// Swap one edge of a near-perfect matching with another edge of the graph
					// to get another near-perfect matching.
					rememberedMatching.addChange(part2Index);
					rememberedMatching.addChange(part1Index);
					rememberedMatching.addChange(currentMatching.adjacency[part1Index]);
					currentMatching.moveEdgeEnd(part1Index, part2Index);
					if (currentMatching.findEdgeToRestoreGoodMatchingToPerfect() != -1) {
						rememberedMatching.updateWith(currentMatching);
					}
				} else if (currentMatching.isType0TargetTransition(part1Index, part2Index)) {
					// Swap one edge of a near-perfect matching with another edge of the graph
					// to get another near-perfect matching (same as the previous case,
					// with different edges to change and unmap).
					rememberedMatching.addChange(part2Index);
					rememberedMatching.addChange(part1Index);
					rememberedMatching.addChange(currentMatching.adjacency[part2Index]);
					currentMatching.moveEdgeEnd(part2Index, part1Index);
					if (currentMatching.findEdgeToRestoreGoodMatchingToPerfect() != -1) {
						rememberedMatching.updateWith(currentMatching);
					}
				}
			}
		}
		if (!currentMatching.isPerfect()) {
			if (rememberedMatching.isPerfect()) {
				currentMatching.loadAnotherAdjacencyVector(rememberedMatching);
			} else {
				currentMatching.restoreNearPerfectMatchingToPerfect(rememberedMatching);
			}
		}
		return currentMatching.createGraph();
	}
}
