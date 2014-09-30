package org.tendiwa.math;

import org.jgrapht.UndirectedGraph;

import java.util.Random;

/**
 * Implements a Markov chain described in "<a href="http://www.cs.berkeley.edu/~sinclair/perm.pdf">Approximating the
 * permanent</a>" by Jerrum and Sinclair.
 */
public class JerrumSinclairMarkovChain {

	private JerrumSinclairMarkovChain() {
		throw new UnsupportedOperationException();
	}

	public static <V, E> Step2<V, E> inGraph(UndirectedGraph<V, E> graph) {
		return new Step2<V, E>(graph);
	}

	public static class Step2<V, E> {
		private final UndirectedGraph<V, E> graph;

		private Step2(UndirectedGraph<V, E> graph) {
			this.graph = graph;
		}

		public Step3<V, E> withInitialMatchingToMutate(UndirectedGraph<V, E> matchingToMutate) {
			return new Step3<>(graph, matchingToMutate);
		}
	}

	public static class Step3<V, E> {
		private final UndirectedGraph<V, E> graph;
		private final UndirectedGraph<V, E> matchingToMutate;

		private Step3(UndirectedGraph<V, E> graph, UndirectedGraph<V, E> matchingToMutate) {
			this.graph = graph;
			this.matchingToMutate = matchingToMutate;
		}

		public Step4<V, E> withNumberOfSteps(int numberOfSteps) {
			return new Step4<>(graph, matchingToMutate, numberOfSteps);
		}
	}

	public static class Step4<V, E> {

		private final UndirectedGraph<V, E> graph;
		private final UndirectedGraph<V, E> matchingToMutate;
		private final int numberOfSteps;

		public Step4(UndirectedGraph<V, E> graph, UndirectedGraph<V, E> matchingToMutate, int numberOfSteps) {

			this.graph = graph;
			this.matchingToMutate = matchingToMutate;
			this.numberOfSteps = numberOfSteps;
		}

		public UndirectedGraph<V, E> withRandom(Random random) {
			return generatePerfectMatching(graph, matchingToMutate, numberOfSteps, random);
		}
	}

	private static <V, E> UndirectedGraph<V, E> generatePerfectMatching(
		UndirectedGraph<V, E> graph,
		UndirectedGraph<V, E> matchingToMutate,
		int numberOfSteps,
		Random random
	) {
		E[] edges = (E[]) graph.edgeSet().toArray();
		boolean completeMatching = true;

		for (int i = 0; i < numberOfSteps; i++) {
			int edgeIndex = (int) Math.floor(edges.length * random.nextDouble());
			E edge = edges[edgeIndex];
			if (isType1Transition(matchingToMutate, completeMatching, edge)) {
				matchingToMutate.removeEdge(edge);
				completeMatching = false;
			} else {
				V source = graph.getEdgeSource(edge);
				V target = graph.getEdgeTarget(edge);
				if (isType2Transition(graph, matchingToMutate, completeMatching, edge)) {
					matchingToMutate.addEdge(source, target, edge);
					completeMatching = true;
				} else if (isType3Source(matchingToMutate, completeMatching, source, target)) {
					matchingToMutate.addEdge(source, target, edge);
					E anotherEdge = matchingToMutate.edgesOf(source).iterator().next();
					V anotherVertex = matchingToMutate.getEdgeSource(anotherEdge) == source ? matchingToMutate
						.getEdgeTarget(anotherEdge) : matchingToMutate.getEdgeSource(anotherEdge);
					matchingToMutate.removeEdge(source, anotherVertex);
					completeMatching = true;
				} else if (isType3Target(matchingToMutate, completeMatching, source, target)) {
					matchingToMutate.addEdge(source, target, edge);
					E anotherEdge = matchingToMutate.edgesOf(target).iterator().next();
					V anotherVertex = matchingToMutate.getEdgeSource(anotherEdge) == target ? matchingToMutate
						.getEdgeTarget(anotherEdge) : matchingToMutate.getEdgeSource(anotherEdge);
					matchingToMutate.removeEdge(source, anotherVertex);
					completeMatching = true;
				}
			}
		}
		return matchingToMutate;
	}

	private static <V, E> boolean isType3Target(UndirectedGraph<V, E> matchingToMutate, boolean completeMatching, V source, V target) {
		return !completeMatching
			&& matchingToMutate.edgesOf(source).isEmpty()
			&& !matchingToMutate.edgesOf(target).isEmpty();
	}

	private static <V, E> boolean isType3Source(UndirectedGraph<V, E> matchingToMutate, boolean completeMatching, V source, V target) {
		return !completeMatching
			&& !matchingToMutate.edgesOf(source).isEmpty()
			&& matchingToMutate.edgesOf(target).isEmpty();
	}

	private static <V, E> boolean isType2Transition(UndirectedGraph<V, E> graph, UndirectedGraph<V, E> matchingToMutate, boolean completeMatching, E edge) {
		return !completeMatching
			&& !matchingToMutate.containsVertex(graph.getEdgeSource(edge))
			&& !matchingToMutate.containsVertex(graph.getEdgeTarget(edge));
	}

	private static <V, E> boolean isType1Transition(UndirectedGraph<V, E> matchingToMutate, boolean completeMatching, E edge) {
		return completeMatching && matchingToMutate.containsEdge(edge);
	}

}

