package org.tendiwa.math;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.NeighborIndex;
import org.tendiwa.drawing.GraphExplorer;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;

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

	/**
	 * Generates a perfect matching with distribution close to uniform from a set of all possible perfect matchings
	 * in a graph.
	 *
	 * @param graph
	 * 	A graph to generate perfect a matching in.
	 * @param matchingToMutate
	 * 	An already constructed perfect matching in {@code graph}. This instance will be mutated AND returned. To
	 * 	construct this matching in the first place, you should use any maximum matching searching algorithm,
	 * 	like {@link org.jgrapht.alg.HopcroftKarpBipartiteMatching}.
	 * @param numberOfSteps
	 * 	How long to simulate Markov chain. The greater the number of steps,
	 * 	the closer distribution is to being uniform, however after certain number of steps gain from adding more
	 * 	steps approaches zero. I'm not sure how to determine the actual number of steps needed for probabilities for
	 * 	each outcome to fall into [(1+ε)^-1, 1+ε] with arbitrarily small ε.
	 * 	fall into
	 * @param random
	 * @param <V>
	 * 	Vertex type.
	 * @param <E>
	 * 	Edge type.
	 * @return A perfect matching with a number of edges equal to that in the initial state of {@code matchingToMutate}.
	 * The matching returned is the {@code matchingToMutate} instance, mutated.
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@code matchingToMutate} is not a valid maximum matching in
	 * 	{@code graph}.
	 * @see <a href="http://mathworld.wolfram.com/PerfectMatching.html">Perfect matching in graph theory</a>
	 */
	private static <V, E> UndirectedGraph<V, E> generatePerfectMatching(
		UndirectedGraph<V, E> graph,
		UndirectedGraph<V, E> matchingToMutate,
		int numberOfSteps,
		Random random
	) {
		if (
			!matchingToMutate.vertexSet().equals(graph.vertexSet())
				|| !graph.edgeSet().containsAll(matchingToMutate.edgeSet())
			) {
			throw new IllegalArgumentException("The matching provided is not a perfect matching in the graph provided");
		}
		int initialNumberOfEdges = matchingToMutate.edgeSet().size();
		E[] edges = (E[]) graph.edgeSet().toArray();
		boolean completeMatching = true;

		int complete = 0;
		int uncomplete = 0;
		for (int i = 0; i < numberOfSteps; i++) {
//			if (i == numberOfSteps * 2) {
//				throw new RuntimeException("Too many steps");
//			}
			int edgeIndex = (int) Math.floor(edges.length * random.nextDouble());
			E edge = edges[edgeIndex];
			if (isType1Transition(matchingToMutate, completeMatching, edge)) {
				matchingToMutate.removeEdge(edge);
				completeMatching = false;
			} else {
				V source = graph.getEdgeSource(edge);
				V target = graph.getEdgeTarget(edge);
				if (isType2Transition(graph, matchingToMutate, completeMatching, edge)) {
					E addedEdge = matchingToMutate.addEdge(source, target);
					completeMatching = true;
					assert addedEdge != null;
				} else if (isType3Source(matchingToMutate, completeMatching, source, target)) {
					E addedEdge = matchingToMutate.addEdge(source, target);
					E anotherEdge = matchingToMutate.edgesOf(source).iterator().next();
					V anotherVertex = matchingToMutate.getEdgeSource(anotherEdge) == source ? matchingToMutate
						.getEdgeTarget(anotherEdge) : matchingToMutate.getEdgeSource(anotherEdge);
					E removedEdge = matchingToMutate.removeEdge(source, anotherVertex);
					assert addedEdge != null && removedEdge != null;
					completeMatching = false;
				} else if (isType3Target(matchingToMutate, completeMatching, source, target)) {
					E addedEdge = matchingToMutate.addEdge(source, target);
					E anotherEdge = matchingToMutate.edgesOf(target).iterator().next();
					V anotherVertex = matchingToMutate.getEdgeSource(anotherEdge) == target ? matchingToMutate
						.getEdgeTarget(anotherEdge) : matchingToMutate.getEdgeSource(anotherEdge);
					E removedEdge = matchingToMutate.removeEdge(target, anotherVertex);
					assert addedEdge != null && removedEdge != null;
					completeMatching = false;
				}
			}
//			System.out.println(completeMatching);
//			printUnmatchedVertices(matchingToMutate);
			if (completeMatching) {
				complete++;
			} else {
				uncomplete++;
			}
		}
		System.out.println(complete + " " + uncomplete);
		if (completeMatching) {
			assert matchingToMutate.vertexSet().equals(graph.vertexSet());
			assert matchingToMutate.edgeSet().size() == initialNumberOfEdges
				: matchingToMutate.edgeSet().size() + " " + initialNumberOfEdges;
			return matchingToMutate;
		} else {
			return getClosestPerfectMatching(matchingToMutate, graph);
		}
	}

	/**
	 * Mutates {@code nearPerfectMatching} so that it is a perfect matching.
	 *
	 * @param nearPerfectMatching
	 * 	A matching with n-1 edges, where n is a number of vertices in each of two
	 * 	partitions in bipartite graph.
	 * @param graph
	 * 	A graph in which matching is constructed.
	 * @param <V>
	 * 	Type of vertices.
	 * @param <E>
	 * 	Type of edges.
	 * @return Mutated {@code nearPerfectMatching} so that it is a perfect matching.
	 */
	private static <V, E> UndirectedGraph<V, E> getClosestPerfectMatching(
		UndirectedGraph<V, E> nearPerfectMatching,
		UndirectedGraph<V, E> graph
	) {
		V[] unmatchedVertices = (V[]) nearPerfectMatching
			.vertexSet()
			.stream()
			.filter(v -> nearPerfectMatching.edgesOf(v).isEmpty())
			.toArray();
		assert unmatchedVertices.length == 2 : Arrays.asList(unmatchedVertices);
		V v = unmatchedVertices[0];
		V u = unmatchedVertices[1];
		System.out.println("Unmatched vertices are " + v + " " + u);
		if (graph.containsEdge(v, u)) {
			nearPerfectMatching.addEdge(v, u);
			System.out.println("mutated by variant 1");
			return nearPerfectMatching;
		}
		NeighborIndex<V, E> neighbors = new NeighborIndex<>(graph);
		Set<V> vNeighbors = neighbors.neighborsOf(v);
		Set<V> uNeighbors = neighbors.neighborsOf(u);
//		new GraphExplorer((UndirectedGraph<Point2D, Segment2D>) nearPerfectMatching, 400, 300, 10);
//		new GraphExplorer((UndirectedGraph<Point2D, Segment2D>) graph, 400, 300, 10);
		for (V u1 : vNeighbors) {
			for (V v1 : uNeighbors) {
				if (nearPerfectMatching.containsEdge(u1, v1)) {
					assert graph.containsEdge(v, u1);
					assert graph.containsEdge(u, v1);
					assert nearPerfectMatching.containsEdge(u1, v1);
					nearPerfectMatching.removeEdge(u1, v1);
					nearPerfectMatching.addEdge(v, u1);
					nearPerfectMatching.addEdge(u, v1);
					System.out.println("mutated by variant 2");
					return nearPerfectMatching;
				}
			}
		}
		new GraphExplorer((UndirectedGraph<Point2D, Segment2D>) nearPerfectMatching, 800, 600, 10);
		new GraphExplorer((UndirectedGraph<Point2D, Segment2D>) graph, 800, 600, 10);
		throw new RuntimeException("Edge v1-u1 not found");
	}


	private static <V, E> void printUnmatchedVertices(UndirectedGraph<V, E> matchingToMutate) {
		matchingToMutate.vertexSet().forEach((v) -> {
			if (matchingToMutate.edgesOf(v).isEmpty()) {
				System.out.println(v);
			}
		});
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

