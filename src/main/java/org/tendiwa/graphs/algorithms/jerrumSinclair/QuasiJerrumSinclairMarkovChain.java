package org.tendiwa.graphs.algorithms.jerrumSinclair;

import org.jgrapht.UndirectedGraph;

import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * This algorithm samples perfect matchings from a bipartite graph at random. Distribution is the closer to uniform
 * the longer this algorithm is run.
 * <p>
 * Based on a Markov chain described in "<a href="http://www.cs.berkeley.edu/~sinclair/perm.pdf">Approximating the
 * permanent</a>" by Jerrum and Sinclair.
 * <p>
 * In the paper it is stated that Jerrum and Sinclair's algorithm works only for dense bipartite graphs (those for
 * which each vertex has degree no less than n/2, where 2n is the number of vertices in graph, and n is the number of
 * vertices in each of two graph's partitions). However, this particular algorithm works for sparse (i.e. not dense)
 * graphs too.
 *
 */
public class QuasiJerrumSinclairMarkovChain {


	public static <V, E> Step1<V, E> inGraph(UndirectedGraph<V, E> graph) {
		Objects.requireNonNull(graph);
		return new Step1<>(graph);
	}

	public static class Step1<V, E> {
		private final UndirectedGraph<V, E> graph;

		private Step1(UndirectedGraph<V, E> graph) {
			this.graph = graph;
		}

		public Step2<V, E> withInitialMatching(Set<E> perfectMatching) {
			Objects.requireNonNull(perfectMatching);
			return new Step2<>(graph, perfectMatching);
		}
	}

	public static class Step2<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final Set<E> perfectMatching;

		private Step2(UndirectedGraph<V, E> graph, Set<E> perfectMatching) {
			this.graph = graph;
			this.perfectMatching = perfectMatching;
		}

		public Step3<V, E> withOneOfPartitions(Set<V> partition) {
			Objects.requireNonNull(partition);
			return new Step3<>(graph, perfectMatching, partition);
		}
	}

	public static class Step3<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final Set<E> perfectMatching;
		private Set<V> partition;

		private Step3(UndirectedGraph<V, E> graph, Set<E> perfectMatching, Set<V> partition) {
			this.graph = graph;
			this.perfectMatching = perfectMatching;
			this.partition = partition;
		}

		public Step4<V, E> withNumberOfSteps(int numberOfSteps) {
			if (numberOfSteps < 0) {
				throw new IllegalArgumentException("number of steps must be >= 0");
			}
			return new Step4<>(graph, perfectMatching, partition, numberOfSteps);
		}
	}

	public static class Step4<V, E> {

		private final UndirectedGraph<V, E> graph;
		private final Set<E> perfectMatching;
		private Set<V> partition;
		private final int numberOfSteps;

		public Step4(UndirectedGraph<V, E> graph, Set<E> perfectMatching, Set<V> partition, int numberOfSteps) {
			this.graph = graph;
			this.perfectMatching = perfectMatching;
			this.numberOfSteps = numberOfSteps;
			this.partition = partition;
		}

		public UndirectedGraph<V, E> withRandom(Random random) {
			Objects.requireNonNull(random);
			return new QuasiJerrumSinclairMarkovChainAlgorithm<>(
				graph,
				partition,
				perfectMatching,
				numberOfSteps,
				random
			).compute();
		}
	}

}

