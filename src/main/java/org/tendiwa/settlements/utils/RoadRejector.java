package org.tendiwa.settlements.utils;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.UndirectedSubgraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.math.StonesInBasketsSolver;
import org.tendiwa.settlements.NetworkWithinCycle;
import org.tendiwa.settlements.RoadsPlanarGraphModel;

import java.util.*;

/**
 * Static methods of this class create a view of a city's road network graph that rejects some of its edges so cities
 * don't have to be enclosed in a road around them (enclosing cities in a road around them is the core mechanics of
 * constructing a {@link org.tendiwa.settlements.RoadsPlanarGraphModel}.
 */
public final class RoadRejector {
	private static final Comparator<Point2D> ANY_NEIGHBOR_COMPARATOR = (p1, p2) -> {
		if (p1 == p2) {
			return 0;
		}
		int compare = Double.compare(p1.x, p2.x);
		if (compare == 0) {
			int compare1 = Double.compare(p1.y, p2.y);
			assert compare1 != 0 : p1 + " " + p2;
			return compare1;
		} else {
			return compare;
		}
	};
	private final UndirectedGraph<Point2D, Segment2D> fullGraph;
	private final Random random;
	private Collection<Segment2D> graphWithGapsUsedChains = new HashSet<>();

	private RoadRejector(UndirectedGraph<Point2D, Segment2D> graph, Random random) {
		fullGraph = graph;
		this.random = new Random(random.nextInt());
	}

	/**
	 * Creates a view of {@code graph} that rejects all the roads that are part of the enclosing cycles of cells of
	 * {@code cityGeometry}.
	 *
	 * @param graph
	 * 	A full road graph ({@link org.tendiwa.settlements.RoadsPlanarGraphModel#getFullRoadGraph()}) of {@code cityGeometry}.
	 * @param roadsPlanarGraphModel
	 * 	A geometry of a city.
	 * @return A view of a road graph without roads forming city's cells' enclosing cycles.
	 */
	public static UndirectedGraph<Point2D, Segment2D> rejectAllCellsBorders(
		UndirectedGraph<Point2D, Segment2D> graph,
		RoadsPlanarGraphModel roadsPlanarGraphModel // TODO: Maybe extract an interface with cycle() method so graph and cityGeometry won't seem coupled
	) {
		UndirectedSubgraph<Point2D, Segment2D> modifiedGraph = new UndirectedSubgraph<>(graph, graph.vertexSet(), graph.edgeSet());
		for (NetworkWithinCycle networkWithinCycle : roadsPlanarGraphModel.getNetworks()) {
			for (Segment2D edge : networkWithinCycle.cycle().edgeSet()) {
				if (modifiedGraph.containsEdge(edge)) {
					modifiedGraph.removeEdge(edge);
					if (modifiedGraph.degreeOf(edge.start) == 0) {
						modifiedGraph.removeVertex(edge.start);
					}
					if (modifiedGraph.degreeOf(edge.end) == 0) {
						modifiedGraph.removeVertex(edge.end);
					}
				}
			}
		}
		return modifiedGraph;
	}

	public static UndirectedGraph<Point2D, Segment2D> rejectPartOfNetworksBorders(
		UndirectedGraph<Point2D, Segment2D> graph,
		RoadsPlanarGraphModel roadsPlanarGraphModel,
		double probability,
		Random random
	) {
		if (probability < 0 || probability > 1) {
			throw new IllegalArgumentException("probability must be in [0..1] (now it is " + probability + ")");
		}
		return new RoadRejector(graph, random).rejectRoads(roadsPlanarGraphModel, probability);
	}

	/**
	 * Removes some of the outer cycle edges
	 *
	 * @param roadsPlanarGraphModel
	 * @param probability
	 * @return
	 */
	private UndirectedGraph<Point2D, Segment2D> rejectRoads(
		RoadsPlanarGraphModel roadsPlanarGraphModel,
		double probability
	) {
		UndirectedSubgraph<Point2D, Segment2D> modifiedGraph = new UndirectedSubgraph<>(
			fullGraph,
			fullGraph.vertexSet(),
			fullGraph.edgeSet()
		);
		List<List<Segment2D>> chains = findChainsBetween3DegreeCycleVertices(roadsPlanarGraphModel);
		int[][] subchainsToRemove = findSubChainsToRemove(chains, probability);
		for (int i = 0; i < subchainsToRemove.length; i++) {
			int firstEdgeIndex = subchainsToRemove[i][0];
			int lastEdgeIndex = firstEdgeIndex + subchainsToRemove[i][1];
			for (int j = firstEdgeIndex; j < lastEdgeIndex; j++) {
				Segment2D edge = chains.get(i).get(j);
				if (modifiedGraph.containsEdge(edge)) {
					PlanarGraphs.removeEdgeAndOrphanedVertices(modifiedGraph, edge);
				}
			}
		}
		return modifiedGraph;

	}

	/**
	 * Decides which sub-chains to remove from cycles.
	 *
	 * @param chains
	 * 	Chains of networks' cycles between those edges that have degree >=2 in the full graph.
	 * @param probability
	 * 	What percentage of edges to remove (may be in range [0..1]).
	 * @return A 2d-array. Index in first dimension is the number of a chain in {@code chains}. Second dimension
	 * contains two numbers: offset of chain and length of a sub-chain to remove.
	 */
	private int[][] findSubChainsToRemove(List<List<Segment2D>> chains, double probability) {
		assert !chains.isEmpty();
		int numberOfEdgesInChains = countAllEdges(chains);
		int numberOfEdgesToRemove = (int) Math.floor((double) numberOfEdgesInChains * probability);
		assert numberOfEdgesToRemove <= numberOfEdgesInChains;
		assert numberOfEdgesToRemove >= 0;
		int[] baskets = createBasketsArray(chains);
		int[] partition = StonesInBasketsSolver.solve(baskets, numberOfEdgesToRemove, random);
		int[][] answer = new int[chains.size()][2];
		for (int i = 0; i < chains.size(); i++) {
			// Offset
			answer[i][0] = (int) Math.floor(random.nextDouble() * (baskets[i] - partition[i] + 1));
			// Length
			answer[i][1] = partition[i];
			assert answer[i][0] + answer[i][1] <= chains.get(i).size();
		}
		return answer;
	}

	private int[] createBasketsArray(List<List<Segment2D>> chains) {
		int[] stones = new int[chains.size()];
		for (int i = 0; i < chains.size(); i++) {
			stones[i] = chains.get(i).size();
		}
		return stones;
	}

	/**
	 * Finds the number of edges in all chains.
	 *
	 * @param chains
	 * 	All chains.
	 * @return Number of edges in all chains.
	 */
	private int countAllEdges(List<List<Segment2D>> chains) {
		int numberOfEdgesInAllChains = 0;
		for (List<Segment2D> chain : chains) {
			numberOfEdgesInAllChains += chain.size();
		}
		return numberOfEdgesInAllChains;
	}

	private List<List<Segment2D>> findChainsBetween3DegreeCycleVertices(RoadsPlanarGraphModel roadsPlanarGraphModel) {
		List<List<Segment2D>> answer = new ArrayList<>();
		for (UndirectedGraph<Point2D, Segment2D> outerCycleEdges : roadsPlanarGraphModel.outerCycleEdges().values()) {
			// Outer cycle edges of a network may be either a continuous cycle (if a network doesn't adjoin other
			// networks of the same pathGeometry), or a set of chains with outerCycleEdges.degreeOf(vertex) == 1 on
			// their ends.
			List<List<Segment2D>> chains = tryFindingChainsAssumingGraphWithGaps(outerCycleEdges);
			if (!chains.isEmpty()) {
				answer.addAll(chains);
			} else {
				assert new ConnectivityInspector<>(outerCycleEdges).isGraphConnected();
				new CycleChainWalker(outerCycleEdges).addIntersectionlessChainsTo(answer);
			}
		}
		assert !answer.isEmpty();
		return answer;
	}

	private class CycleChainWalker {

		private final UndirectedGraph<Point2D, Segment2D> outerCycleEdges;
		private Point2D firstVertex = null;
		private Point2D previousVertex = null;
		private Point2D currentVertex = null;

		CycleChainWalker(UndirectedGraph<Point2D, Segment2D> outerCycleEdges) {
			this.outerCycleEdges = outerCycleEdges;
			findInitialVertices(outerCycleEdges);
			assert outerCycleEdges.vertexSet().stream().allMatch(v -> outerCycleEdges.degreeOf(v) == 2);
		}

		void addIntersectionlessChainsTo(List<List<Segment2D>> answer) {
			List<Segment2D> chain = new LinkedList<>();
			do {
				Segment2D cycleEdge = outerCycleEdges.getEdge(previousVertex, currentVertex);
				chain.add(cycleEdge);
				if (fullGraph.degreeOf(currentVertex) > 2) {
					assert fullGraph.degreeOf(chain.get(0).start) > 2
						|| fullGraph.degreeOf(chain.get(0).end) > 2;
					assert fullGraph.degreeOf(chain.get(chain.size() - 1).start) > 2
						|| fullGraph.degreeOf(chain.get(chain.size() - 1).end) > 2;
					answer.add(chain);
					chain = new LinkedList<>();
				}
				Point2D bufCurrentVertex = currentVertex;
				currentVertex = getNextVertex(previousVertex, currentVertex);
				previousVertex = bufCurrentVertex;
			} while (previousVertex != firstVertex);
		}

		private void findInitialVertices(UndirectedGraph<Point2D, Segment2D> outerCycleEdges) {
			for (Point2D vertex : outerCycleEdges.vertexSet()) {
				if (fullGraph.degreeOf(vertex) > 2) {
					firstVertex = vertex;
					previousVertex = vertex;
					currentVertex = selectAnyNeighbor(vertex, outerCycleEdges);
					break;
				}
			}
			if (firstVertex == null) {
				assert previousVertex == null;
				assert currentVertex == null;
				throw new RuntimeException("Initial vertices not found");
			}
		}

		private Point2D getNextVertex(Point2D previousVertex, Point2D currentVertex) {
			assert outerCycleEdges.edgesOf(currentVertex).size() == 2;
			for (Segment2D edge : outerCycleEdges.edgesOf(currentVertex)) {
				if (edge.start == previousVertex) {
					continue;
				}
				if (edge.end == previousVertex) {
					continue;
				}
				if (edge.start == currentVertex) {
					return edge.end;
				}
				assert edge.end == currentVertex;
				return edge.start;
			}
			throw new RuntimeException("Next vertex not found");
		}
	}

	private List<List<Segment2D>> tryFindingChainsAssumingGraphWithGaps(UndirectedGraph<Point2D, Segment2D> outerCycleEdges) {
		List<List<Segment2D>> answer = new ArrayList<>(10);
		Collection<Point2D> usedVertices = new HashSet<>();
		for (Point2D vertex : outerCycleEdges.vertexSet()) {
			if (usedVertices.contains(vertex)) {
				continue;
			}
			if (outerCycleEdges.degreeOf(vertex) == 1) {
				new NonCycleChainWalker(outerCycleEdges, vertex).addIntersectionlessChainsTo(answer);
			}
			usedVertices.add(vertex);
		}
		return answer;
	}

	/**
	 * Walks along chains of edges in a subgraph of {@link #fullGraph} ({@code outerCycleEdges}),
	 * extracting the sub-chains that start and end with vertices having degree > 2 in {@link #fullGraph}.
	 *
	 * @see org.tendiwa.settlements.RoadsPlanarGraphModel#outerCycleEdges() for what kind of subgraph is walked along.
	 */
	private class NonCycleChainWalker {
		private final UndirectedGraph<Point2D, Segment2D> outerCycleEdges;
		private Point2D previousVertex;
		private Segment2D currentEdge;
		private Point2D currentVertex;
		private List<Segment2D> chain;

		private NonCycleChainWalker(UndirectedGraph<Point2D, Segment2D> outerCycleEdges, Point2D startVertex) {
			this.outerCycleEdges = outerCycleEdges;
			this.previousVertex = startVertex;
			Set<Segment2D> edgesOfVertex = outerCycleEdges.edgesOf(startVertex);
			assert edgesOfVertex.size() == 1;
			this.currentEdge = edgesOfVertex.iterator().next();
			this.currentVertex = currentEdge.start == previousVertex ? currentEdge.end : currentEdge.start;
			assert outerCycleEdges.getEdge(previousVertex, currentVertex) == currentEdge;
		}

		/**
		 * Within {@link #outerCycleEdges}, finds chains that start with non-2-degree vertices and contain only
		 * 2-degree
		 * vertices between ends, and adds those chains to {@code answer}.
		 *
		 * @param answer
		 * 	A collection to add found chains to.
		 */
		private void addIntersectionlessChainsTo(List<List<Segment2D>> answer) {
			chain = new LinkedList<>();
			chain.add(currentEdge);
			do {
				moveToNextEdge();
				assert outerCycleEdges.getEdge(previousVertex, currentVertex) == currentEdge;
				chain.add(currentEdge);
				if (fullGraph.degreeOf(currentVertex) > 2 && outerCycleEdges.degreeOf(currentVertex) == 1) {
					answer.add(chain);
					chain = new LinkedList<>();
				}
			} while (outerCycleEdges.degreeOf(currentVertex) == 2);
			assert outerCycleEdges.degreeOf(currentVertex) == 1; // We stop at the other end of a chain.
			assert chain.isEmpty() : chain.size(); // There are no non-flushed segments.
		}

		/**
		 * Changes {@link #previousVertex} and {@link #currentVertex} to be the ends of the next edge in chain,
		 * and remembers that edge as {@link #currentEdge}.
		 */
		private void moveToNextEdge() {
			Set<Segment2D> edgesOfNextVertex = outerCycleEdges.edgesOf(currentVertex);
			assert edgesOfNextVertex.size() == 2;
			for (Segment2D edge : edgesOfNextVertex) {
				if (edge.start == currentVertex && edge.end != previousVertex) {
					currentEdge = edge;
					previousVertex = currentVertex;
					currentVertex = edge.end;
					break;
				} else if (edge.end == currentVertex && edge.start != previousVertex) {
					currentEdge = edge;
					previousVertex = currentVertex;
					currentVertex = edge.start;
					break;
				}
			}
		}
	}


	private Point2D selectAnyNeighbor(Point2D vertex, UndirectedGraph<Point2D, Segment2D> cycle) {
		Set<Segment2D> edges = cycle.edgesOf(vertex);
		// Sorted collection is used here so ordering will remain constant between application start-ups.
		SortedSet<Point2D> possibleAnyNeighbor = new TreeSet<>(ANY_NEIGHBOR_COMPARATOR);
		for (Segment2D edge : edges) {
			if (!possibleAnyNeighbor.contains(edge.start)) {
				possibleAnyNeighbor.add(edge.start);
			}
			if (!possibleAnyNeighbor.contains(edge.end)) {
				possibleAnyNeighbor.add(edge.end);
			}
		}
		return possibleAnyNeighbor.last();
	}
}
