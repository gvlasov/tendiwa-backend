package org.tendiwa.settlements.buildings;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.graphs.algorithms.jerrumSinclair.QuasiJerrumSinclairMarkovChain;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;
import org.tendiwa.terrain.WorldGenerationException;

import java.util.*;

/**
 * Finds a mapping from lots to architecture that satisfies architecture policies.
 */
final class UrbanPlanningStrategy {
	private final Map<ArchitecturePolicy, Architecture> architecture;
	private final LotsTouchingStreets lotsTouchingStreets;
	private final Set<RectangleWithNeighbors> lots;
	private final Random random;
	private static final EdgeFactory<Object, DefaultEdge> edgeFactory = (a, b) -> new DefaultEdge();
	private final Set<RectangleWithNeighbors> usedLots = new HashSet<>();
	private final LinkedHashMap<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> possiblePlaces;
	public static final Partition1Vertex IMAGINARY_POLICY_FOR_THE_REST = new Partition1Vertex(null);

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
		this.lotsTouchingStreets = lotsTouchingStreets;
		this.lots = lots;
		this.random = new Random(random.nextInt());
		this.possiblePlaces = findPossiblePlaces(architecture, lots);
	}

	public Map<RectangleWithNeighbors, Architecture> compute() {
		validateEnoughLotsForMinInstancesConstraint();

		UndirectedGraph<Object, DefaultEdge> bigraph = new SimpleGraph<>(edgeFactory);
		Set<Object> partition1 = new LinkedHashSet<>();
		Set<Object> partition2 = new LinkedHashSet<>();

		Map<RectangleWithNeighbors, Architecture> answer = new LinkedHashMap<>();
		int lotsClaimed = 0;
		for (ArchitecturePolicy policy : architecture.keySet()) {
			for (int i = 0; i < policy.minInstances; i++) {
				Partition1Vertex needForPlace = new Partition1Vertex(policy);
				partition1.add(needForPlace);
				bigraph.addVertex(needForPlace);
				for (RectangleWithNeighbors place : possiblePlaces.get(policy)) {
					partition2.add(place);
					bigraph.addVertex(place);
					bigraph.addEdge(needForPlace, place);
				}
				lotsClaimed += policy.minInstances;
			}
		}
		addImaginaryPolicyForTheRestOfLots(bigraph, lotsClaimed);


		UndirectedGraph<Object, DefaultEdge> generatedMaximumMatching = generateMaximumMatching(
			bigraph,
			partition1,
			partition2
		);
		putAssignmentsToAnswerMap(answer, generatedMaximumMatching);
		int minInstancesSum = architecture.keySet().stream().map(p -> p.minInstances).reduce(0, (a, b) -> a + b);
		assert answer.size() >= minInstancesSum : answer.size() + " " + minInstancesSum;


		addToAnswerUntilMaximumsForPoliciesIsReached(answer);

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
	private void putAssignmentsToAnswerMap(Map<RectangleWithNeighbors, Architecture> answerMap, UndirectedGraph<Object, DefaultEdge> generatedMaximumMatching) {
		for (DefaultEdge edge : generatedMaximumMatching.edgeSet()) {
			ArchitecturePolicy policy;
			RectangleWithNeighbors lot;
			Object source = generatedMaximumMatching.getEdgeSource(edge);
			if (source instanceof Partition1Vertex) {
				policy = ((Partition1Vertex) source).policy;
				lot = (RectangleWithNeighbors) generatedMaximumMatching.getEdgeTarget(edge);
			} else {
				assert source instanceof RectangleWithNeighbors;
				policy = ((Partition1Vertex) generatedMaximumMatching.getEdgeTarget(edge)).policy;
				lot = (RectangleWithNeighbors) source;
			}

			answerMap.put(lot, architecture.get(policy));
			usedLots.add(lot);
		}
	}

	private UndirectedGraph<Object, DefaultEdge> generateMaximumMatching(UndirectedGraph<Object, DefaultEdge> bigraph, Set<Object> partition1, Set<Object> partition2) {
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
		generatedMaximumMatching.removeVertex(IMAGINARY_POLICY_FOR_THE_REST);
		return generatedMaximumMatching;
	}

	/**
	 * Adds a new vertex with its edges into {@code bigraph} for bigraph to have enough edges to contain a perfect
	 * matching.
	 *
	 * @param bigraph
	 * 	A bigraph to add an extra vertex and 1+ extra edges to.
	 * @param lotsClaimed
	 * 	How many lots are already claimed by actual (as opposed to the imaginary one being
	 * 	added) policies.
	 * @see org.tendiwa.graphs.algorithms.jerrumSinclair.QuasiJerrumSinclairMarkovChain
	 */
	private void addImaginaryPolicyForTheRestOfLots(UndirectedGraph<Object, DefaultEdge> bigraph, int lotsClaimed) {
		// This policy will want the number of lots lacking for a configuration to be a perfect matching in bigraph
		int numberOfAllLots = lots.size();
		Iterator<ArchitecturePolicy> iter = possiblePlaces.keySet().iterator();
		bigraph.addVertex(IMAGINARY_POLICY_FOR_THE_REST);
		while (lotsClaimed < numberOfAllLots) {
			assert iter.hasNext();
			ArchitecturePolicy policy = iter.next();
			LinkedHashSet<RectangleWithNeighbors> lotsForPolicy = possiblePlaces.get(policy);
			int numberOfLotsForPolicy = policy.minInstances;
			int numberOfLotsLeftToFill = numberOfAllLots - lotsClaimed;
			if (numberOfLotsForPolicy < numberOfLotsLeftToFill) {
				for (RectangleWithNeighbors lot : lotsForPolicy) {
					bigraph.addEdge(IMAGINARY_POLICY_FOR_THE_REST, lot);
				}
				lotsClaimed += numberOfLotsForPolicy;
			} else {
				Iterator<RectangleWithNeighbors> iter2 = lotsForPolicy.iterator();
				for (int i = 0; i < numberOfLotsLeftToFill; i++) {
					bigraph.addEdge(IMAGINARY_POLICY_FOR_THE_REST, iter2.next());
				}
				lotsClaimed += numberOfLotsLeftToFill;
			}
		}
		assert lotsClaimed == numberOfAllLots;
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
	 * Maps lots to policies until {@link org.tendiwa.settlements.buildings.ArchitecturePolicy#maxInstances} of all
	 * policies prevent remaining lots from being added.
	 *
	 * @param answer
	 * 	A map to collect all mappings of lots to policies.
	 */
	private void addToAnswerUntilMaximumsForPoliciesIsReached(Map<RectangleWithNeighbors, Architecture> answer) {
		TObjectIntMap<ArchitecturePolicy> policiesThatNeedLot = preparePoliciesThatNeedLot(possiblePlaces);
		while (!policiesThatNeedLot.isEmpty()) {
			ArchitecturePolicy policyThatNeedsLot;
			for (RectangleWithNeighbors lot : lots) {
				policyThatNeedsLot = chooseRandomPolicyThatNeedsLot(policiesThatNeedLot);
				if (!usedLots.contains(lot)) {
					usedLots.add(lot);
					answer.put(lot, architecture.get(policyThatNeedsLot));
					if (policiesThatNeedLot.get(policyThatNeedsLot) == 1) {
						policiesThatNeedLot.remove(policyThatNeedsLot);
					} else {
						policiesThatNeedLot.adjustValue(policyThatNeedsLot, -1);
					}
				}
			}
		}
	}

	private TObjectIntMap<ArchitecturePolicy> preparePoliciesThatNeedLot(LinkedHashMap<ArchitecturePolicy,
		LinkedHashSet<RectangleWithNeighbors>> possiblePlaces) {
		TObjectIntMap<ArchitecturePolicy> policiesThatNeedLot = new TObjectIntHashMap<>(
			possiblePlaces.keySet().size()
		);
		for (Map.Entry<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> e : possiblePlaces.entrySet()) {
			int lotsLeftToOccupy = Math.min(e.getValue().size(), e.getKey().maxInstances) - e.getKey().minInstances;
			assert lotsLeftToOccupy >= 0;
			if (lotsLeftToOccupy > 0) {
				policiesThatNeedLot.put(e.getKey(), lotsLeftToOccupy);
			}
		}
		return policiesThatNeedLot;
	}

	private ArchitecturePolicy chooseRandomPolicyThatNeedsLot(TObjectIntMap<ArchitecturePolicy> policiesThatNeedLot) {
		int size = policiesThatNeedLot.size();
		return ((ArchitecturePolicy[]) policiesThatNeedLot.keys())
			[(int) Math.floor((size + 1) * random.nextDouble())];
	}

	/**
	 * Searches for sets of lots that are defined with the following constraints:
	 * <ul>
	 * <li>
	 * {@link org.tendiwa.settlements.buildings.ArchitecturePolicy#onStreet};
	 * </li>
	 * <li>
	 * {@link org.tendiwa.settlements.buildings.ArchitecturePolicy#allowedArea};
	 * </li>
	 * <li>
	 * {@link org.tendiwa.settlements.buildings.Architecture#fits(org.tendiwa.settlements.RectangleWithNeighbors)} )}.
	 * </li>
	 * </ul>
	 * <p>
	 * These constraints are dependency-less, that is, satisfying a constraint can't be a cause of not satisfying
	 * another constraint.
	 *
	 * @param architectures
	 * 	Architectures and their policies.
	 * @param allPlaces
	 * 	All available unoccupied lots.
	 * @return A map from Architecture to a set of lots where it can be placed.
	 */
	private LinkedHashMap<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> findPossiblePlaces(
		Map<ArchitecturePolicy, Architecture> architectures,
		Set<RectangleWithNeighbors> allPlaces
	) {
		LinkedHashMap<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> answer = new LinkedHashMap<>();
		for (Map.Entry<ArchitecturePolicy, Architecture> e : architectures.entrySet()) {
			Architecture architecture = e.getValue();
			ArchitecturePolicy policy = e.getKey();
			LinkedHashSet<RectangleWithNeighbors> places = new LinkedHashSet<>();
			answer.put(policy, places);
			Collection<LinkedHashSet<RectangleWithNeighbors>> setsByConstraints = new ArrayList<>(3);
			if (!policy.onStreet.isEmpty()) {
				for (Street street : policy.onStreet) {
					setsByConstraints.add(new LinkedHashSet<>(lotsTouchingStreets.getLotsOnStreet(street)));
				}
			}
			if (policy.allowedArea != null) {
				LinkedHashSet<RectangleWithNeighbors> allowedArea = new LinkedHashSet<>();
				Rectangle boundingRec = policy.allowedArea.getBounds();
				for (RectangleWithNeighbors place : allPlaces) {
					Optional<Rectangle> intersection = boundingRec.intersectionWith(place.rectangle);
					if (intersection.isPresent() && intersection.get().equals(place.rectangle)) {
						allowedArea.add(place);
					}
				}
				Iterator<RectangleWithNeighbors> iter = allowedArea.iterator();
				while (iter.hasNext()) {
					RectangleWithNeighbors rectangle = iter.next();
					if (!Recs.placeableContainsRectangle(policy.allowedArea, rectangle.rectangle)) {
						iter.remove();
					}
				}

				setsByConstraints.add(allowedArea);
			}
			// Leave only those sets that satisfy certain policy constraints (not all policy constraints,
			// only the simplest ones).
			if (setsByConstraints.isEmpty()) {
				answer.put(policy, new LinkedHashSet<>(allPlaces));
			} else {
				Iterator<LinkedHashSet<RectangleWithNeighbors>> iter = setsByConstraints.iterator();
				LinkedHashSet<RectangleWithNeighbors> allConstraintsApplied = iter.next();
				while (iter.hasNext()) {
					Set<RectangleWithNeighbors> anotherConstraint = iter.next();
					allConstraintsApplied.retainAll(anotherConstraint);
				}
				answer.put(policy, allConstraintsApplied);
			}
			Iterator<RectangleWithNeighbors> iter = answer.get(policy).iterator();
			while (iter.hasNext()) {
				// Leave only those lots where architecture fits
				RectangleWithNeighbors rec = iter.next();
				if (!architecture.fits(rec)) {
					iter.remove();
				}
			}
		}
		return answer;
	}

	private static class Partition1Vertex {
		private final ArchitecturePolicy policy;

		public Partition1Vertex(ArchitecturePolicy policy) {
			this.policy = policy;
		}
	}
}
