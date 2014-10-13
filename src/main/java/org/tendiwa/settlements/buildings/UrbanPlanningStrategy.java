package org.tendiwa.settlements.buildings;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.graphs.algorithms.jerrumSinclair.QuasiJerrumSinclairMarkovChain;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.terrain.WorldGenerationException;

import java.util.*;

/**
 * Finds a mapping from lots to architecture that satisfies architecture policies.
 */
final class UrbanPlanningStrategy {
	private final Map<ArchitecturePolicy, Architecture> architecture;
	private final Set<RectangleWithNeighbors> lots;
	private final Random random;
	private static final EdgeFactory<Object, DefaultEdge> edgeFactory = (a, b) -> new DefaultEdge();
	private final Set<RectangleWithNeighbors> usedLots = new HashSet<>();
	private final LinkedHashMap<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> possiblePlaces;

	/**
	 * @param architecture
	 * @param lotsTouchingStreets
	 * @param lots
	 * @param random
	 * 	Seeded random.
	 */
	UrbanPlanningStrategy(
		Map<ArchitecturePolicy, Architecture> architecture,
		LotsTouchingStreets lotsTouchingStreets,
		Set<RectangleWithNeighbors> lots,
		Random random
	) {
		this.architecture = architecture;
		this.lots = lots;
		this.random = new Random(random.nextInt());
		this.possiblePlaces = new PossiblePlacesFinder(lotsTouchingStreets)
			.findPossiblePlaces(architecture, lots);
	}

	/**
	 * Generates a configuration of what {@link Architecture} is going to what {@link
	 * org.tendiwa.settlements.RectangleWithNeighbors} based on
	 * what policies are assigned to those Architectures.
	 *
	 * @return A mapping from lots to architecture in those lots.
	 */
	public Map<RectangleWithNeighbors, Architecture> compute() {
		validateEnoughLotsForMinInstancesConstraint();

		UndirectedGraph<Object, DefaultEdge> bipartiteGraph = new SimpleGraph<>(edgeFactory);
		Set<Object> partition1 = new LinkedHashSet<>();
		Set<Object> partition2 = new LinkedHashSet<>();

		Map<RectangleWithNeighbors, Architecture> answer = new LinkedHashMap<>();
		int lotsClaimed = 0;
		for (ArchitecturePolicy policy : architecture.keySet()) {
			int minInstances = policy.getAcualMinInstances(possiblePlaces.get(policy).size());
			for (int i = 0; i < minInstances; i++) {
				NeedForPlace needForPlace = new NeedForPlace(policy);
				partition1.add(needForPlace);
				bipartiteGraph.addVertex(needForPlace);
				for (RectangleWithNeighbors place : possiblePlaces.get(policy)) {
					partition2.add(place);
					bipartiteGraph.addVertex(place);
					bipartiteGraph.addEdge(needForPlace, place);
				}
				lotsClaimed += minInstances;
				assert lotsClaimed <= lots.size();
			}
		}
		assert partition2.size() <= lots.size();
		addImaginaryPolicyForTheRestOfLots(bipartiteGraph, lotsClaimed, partition1, partition2);

		UndirectedGraph<Object, DefaultEdge> generatedMaximumMatching = generateMaximumMatching(
			bipartiteGraph,
			partition1,
			partition2
		);
		putMandatoryAssignments(answer, generatedMaximumMatching);
		int minInstancesSum = architecture
			.keySet()
			.stream()
			.map(p -> p.getAcualMinInstances(possiblePlaces.get(p).size()))
			.reduce(0, (a, b) -> a + b);
		assert answer.size() >= minInstancesSum : answer.size() + " " + minInstancesSum;
		putArbitraryAssignments(answer);
		return answer;
	}

	/**
	 * Fills {@code answerMap} with assignments of lots to policies.
	 * <p>
	 * f(lot)->policy is surjective.
	 *
	 * @param answerMap
	 * 	A map to put assignments to.
	 * @param generatedMaximumMatching
	 * 	A matching, where each edge is an assignment of a policy to a lot.
	 */
	private void putMandatoryAssignments(
		Map<RectangleWithNeighbors, Architecture> answerMap,
		UndirectedGraph<Object, DefaultEdge> generatedMaximumMatching
	) {
		for (DefaultEdge edge : generatedMaximumMatching.edgeSet()) {
			ArchitecturePolicy policy;
			RectangleWithNeighbors lot;
			Object source = generatedMaximumMatching.getEdgeSource(edge);
			if (source instanceof NeedForPlace) {
				policy = ((NeedForPlace) source).policy;
				lot = (RectangleWithNeighbors) generatedMaximumMatching.getEdgeTarget(edge);
			} else if (source instanceof RectangleWithNeighbors) {
				policy = ((NeedForPlace) generatedMaximumMatching.getEdgeTarget(edge)).policy;
				lot = (RectangleWithNeighbors) source;
			} else {
				assert source instanceof ImaginaryNeedForPlace
					|| generatedMaximumMatching.getEdgeTarget(edge) instanceof ImaginaryNeedForPlace;
				continue;
			}

			answerMap.put(lot, architecture.get(policy));
			usedLots.add(lot);
		}
	}

	private UndirectedGraph<Object, DefaultEdge> generateMaximumMatching(
		UndirectedGraph<Object, DefaultEdge> bigraph,
		Set<Object> partition1,
		Set<Object> partition2
	) {
		Set<DefaultEdge> maximumMatchingEdges = new HopcroftKarpBipartiteMatching<>(bigraph, partition1, partition2).getMatching();
		UndirectedGraph<Object, DefaultEdge> matchingToMutate = new SimpleGraph<>(edgeFactory);
		for (Object vertex : bigraph.vertexSet()) {
			matchingToMutate.addVertex(vertex);
		}
		for (DefaultEdge edge : maximumMatchingEdges) {
			matchingToMutate.addEdge(bigraph.getEdgeSource(edge), bigraph.getEdgeTarget(edge), edge);
		}

		assert matchingToMutate.edgeSet().size() == partition1.size();
		UndirectedGraph<Object, DefaultEdge> generatedMaximumMatching = QuasiJerrumSinclairMarkovChain
			.inGraph(bigraph)
			.withInitialMatching(maximumMatchingEdges)
			.withOneOfPartitions(partition1)
			.withNumberOfSteps(100)
			.withRandom(random);
		for (Object vertex : partition1) {
			if (vertex instanceof ImaginaryNeedForPlace) {
				generatedMaximumMatching.removeVertex(vertex);
			}
		}
		return generatedMaximumMatching;
	}

	/**
	 * Adds a new vertex with its edges into {@code bipartiteGraph} for bipartite graph to have enough edges to
	 * contain a perfect matching.
	 *
	 * @param bipartiteGraph
	 * 	A bipartite graph to add an extra vertex and 1+ extra edges to.
	 * @param lotsClaimed
	 * 	How many lots are already claimed by actual (as opposed to the imaginary one being
	 * 	added) policies.
	 * @param partition1
	 * @param partition2
	 * @see org.tendiwa.graphs.algorithms.jerrumSinclair.QuasiJerrumSinclairMarkovChain
	 */
	private void addImaginaryPolicyForTheRestOfLots(
		UndirectedGraph<Object, DefaultEdge> bipartiteGraph,
		int lotsClaimed,
		Set<Object> partition1,
		Set<Object> partition2
	) {
		int numberOfAllLots = lots.size();
		for (RectangleWithNeighbors lot : lots) {
			// Lots that are already present in partition2 will just not be added.
			partition2.add(lot);
		}

		for (int i = lotsClaimed; i < numberOfAllLots; i++) {
			ImaginaryNeedForPlace imaginaryPolicyNeed = new ImaginaryNeedForPlace();
			bipartiteGraph.addVertex(imaginaryPolicyNeed);
			for (RectangleWithNeighbors lot : lots) {
				bipartiteGraph.addEdge(imaginaryPolicyNeed, lot);
				partition1.add(imaginaryPolicyNeed);
			}
		}
//		// This policy will want the number of lots lacking for a configuration
//		// to be a perfect matching in bipartite graph.
//		assert lotsClaimed < numberOfAllLots;
//		Iterator<ArchitecturePolicy> iter = possiblePlaces.keySet().iterator();
//		bipartiteGraph.addVertex(IMAGINARY_POLICY_FOR_THE_REST);
//		while (lotsClaimed < numberOfAllLots) {
//			assert iter.hasNext();
//			ArchitecturePolicy policy = iter.next();
//			LinkedHashSet<RectangleWithNeighbors> lotsForPolicy = possiblePlaces.get(policy);
//			int lotsAvailableToPolicy = possiblePlaces.get(policy).size();
//			int lotsNotClaimedForPolicy = lotsAvailableToPolicy - policy.getAcualMinInstances(lotsAvailableToPolicy);
//			int lotsLeftToFill = numberOfAllLots - lotsClaimed;
//			if (lotsNotClaimedForPolicy < lotsLeftToFill) {
//				for (RectangleWithNeighbors lot : lotsForPolicy) {
//					bipartiteGraph.addEdge(IMAGINARY_POLICY_FOR_THE_REST, lot);
//				}
//				lotsClaimed += lotsNotClaimedForPolicy;
//				assert lotsClaimed < numberOfAllLots;
//			} else {
//				Iterator<RectangleWithNeighbors> iter2 = lotsForPolicy.iterator();
//				for (int i = 0; i < lotsLeftToFill; i++) {
//					bipartiteGraph.addEdge(IMAGINARY_POLICY_FOR_THE_REST, iter2.next());
//				}
//				lotsClaimed += lotsLeftToFill;
//				assert lotsClaimed < numberOfAllLots;
//			}
//		}
//		assert lotsClaimed == numberOfAllLots;
	}

	/**
	 * Checks that each policy has access to at least {@code policy.minInstances} lots.
	 *
	 * @throws org.tendiwa.terrain.WorldGenerationException
	 * 	if there are not enough lots where a policy can put its
	 * 	architecture due to various constraints.
	 */
	private void validateEnoughLotsForMinInstancesConstraint() {
		for (Map.Entry<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> e : possiblePlaces.entrySet()) {
			if (e.getKey().minInstances > e.getValue().size()) {
				throw new WorldGenerationException(
					"Can't place a minimum of " + e.getKey().minInstances + " instances of architecture "
						+ architecture.get(e.getKey()).getClass().getSimpleName() + " into "
						+ e.getValue().size() + " places"
				);
			}
		}
	}

	/**
	 * Maps lots to policies until {@link org.tendiwa.settlements.buildings.ArchitecturePolicy#maxInstances} of each
	 * policy prevent remaining lots from being added.
	 *
	 * @param answer
	 * 	A map to collect all the resulting mappings of lots to policies.
	 */
	private void putArbitraryAssignments(
		Map<RectangleWithNeighbors, Architecture> answer
	) {
		TObjectIntMap<ArchitecturePolicy> policiesThatNeedLot = preparePoliciesThatNeedLots(possiblePlaces);
		while (!policiesThatNeedLot.isEmpty()) {
			boolean found = false;
			for (RectangleWithNeighbors lot : lots) {
				ArchitecturePolicy policyThatNeedsLot = chooseRandomPolicyThatNeedsLot(policiesThatNeedLot);
				if (!usedLots.contains(lot)) {
					found = true;
					usedLots.add(lot);
					answer.put(lot, architecture.get(policyThatNeedsLot));
					if (policiesThatNeedLot.get(policyThatNeedsLot) == 1) {
						policiesThatNeedLot.remove(policyThatNeedsLot);
					} else {
						policiesThatNeedLot.adjustValue(policyThatNeedsLot, -1);
					}
				}
			}
			assert found;
		}
	}

	private TObjectIntMap<ArchitecturePolicy> preparePoliciesThatNeedLots(
		LinkedHashMap<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> possiblePlaces
	) {
		TObjectIntMap<ArchitecturePolicy> policiesThatNeedLot = new TObjectIntHashMap<>(
			possiblePlaces.keySet().size()
		);

		for (Map.Entry<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> e : possiblePlaces.entrySet()) {
			ArchitecturePolicy policy = e.getKey();
			LinkedHashSet<RectangleWithNeighbors> availableLots = e.getValue();
			int availableLotsSize = availableLots.size();
			int lotsLeftToOccupy = Math.min(availableLotsSize, policy.maxInstances)
				- policy.getAcualMinInstances(availableLotsSize);
			assert lotsLeftToOccupy >= 0;
			if (lotsLeftToOccupy > 0) {
				policiesThatNeedLot.put(policy, lotsLeftToOccupy);
			}
		}
		return policiesThatNeedLot;
	}

	private ArchitecturePolicy chooseRandomPolicyThatNeedsLot(
		TObjectIntMap<ArchitecturePolicy> policiesThatNeedLot
	) {
		int size = policiesThatNeedLot.size();
		return (ArchitecturePolicy) policiesThatNeedLot.keys()
			[(int) Math.floor(size * random.nextDouble())];
	}


	private static class NeedForPlace {
		private final ArchitecturePolicy policy;

		public NeedForPlace(ArchitecturePolicy policy) {
			assert policy != null;
			this.policy = policy;
		}
	}

	/**
	 * With instances of this class, we fill a bipartite graph to compute a perfect matching.
	 */
	private static class ImaginaryNeedForPlace {

	}
}
