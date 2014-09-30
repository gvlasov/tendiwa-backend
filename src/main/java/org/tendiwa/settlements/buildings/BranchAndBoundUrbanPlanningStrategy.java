package org.tendiwa.settlements.buildings;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.math.JerrumSinclairMarkovChain;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;

import java.util.*;

/**
 * Finds a mapping from lots to architecture that satisfies architecture policies.
 */
final class BranchAndBoundUrbanPlanningStrategy {
	private final Map<ArchitecturePolicy, Architecture> architecture;
	private final LotsTouchingStreets lotsTouchingStreets;
	private final Set<RectangleWithNeighbors> lots;
	private final Random random;
	private final Map<RectangleWithNeighbors, Collection<ArchitecturePolicy>> policyConstrainedLots = new IdentityHashMap<>();
	/**
	 * Compares two lots so they can be sorted.
	 * <p>
	 * Lot with greater number of policies wanting it is greater. If two lots have equal number of lots
	 * available to them, then the greatest is the lot that has its top-left y coordinate greater. If both lots'
	 * y-coordinates are equal, same check is applied to x-coordinates.
	 * <p>
	 * This comparator is never intended to return 0, i.e. it never considers two elements equal.
	 * <p>
	 * This comparator is used for this planning strategy to produce deterministic results.
	 * {@link #policyConstrainedLots} must contain only deterministic collections for this comparator to return
	 * deterministic results.
	 */
	private final Comparator<RectangleWithNeighbors> POLICY_COMPARATOR = (a, b) -> {
		int dLots = policyConstrainedLots.get(a).size() - policyConstrainedLots.get(b).size();
		if (dLots != 0) {
			return dLots;
		}
		int dy = a.rectangle.y - b.rectangle.y;
		if (dy > 0) {
			return 1;
		}
		if (dy < 0) {
			return -1;
		}
		int dx = a.rectangle.x - b.rectangle.x;
		if (dx > 0) {
			return 1;
		}
		assert dx < 0;
		return -1;
	};
	private final TreeSet<RectangleWithNeighbors> sortedLots = new TreeSet<>(POLICY_COMPARATOR);
	private static final EdgeFactory<Object, DefaultEdge> edgeFactory = (a, b) -> new DefaultEdge();

	/**
	 * @param architecture
	 * @param lotsTouchingStreets
	 * @param lots
	 * @param random
	 * 	Seeded random.
	 */
	BranchAndBoundUrbanPlanningStrategy(
		Map<ArchitecturePolicy, Architecture> architecture,
		LotsTouchingStreets lotsTouchingStreets,
		Set<RectangleWithNeighbors> lots,
		Random random
	) {
		this.architecture = architecture;
		this.lotsTouchingStreets = lotsTouchingStreets;
		this.lots = lots;
		this.random = random;
	}

	public Map<RectangleWithNeighbors, Architecture> compute() {
//		DependencyGraph dependencyGraph = new DependencyGraph(architecture);
		Map<ArchitecturePolicy, Set<RectangleWithNeighbors>> possiblePlaces = findPossiblePlaces(architecture, lots);
		initSortedLots(possiblePlaces);
		UndirectedGraph<Object, DefaultEdge> bigraph = new SimpleGraph<>(edgeFactory);
		Set<Object> partition1 = new LinkedHashSet<>();
		Set<Object> partition2 = new LinkedHashSet<>();

		Map<RectangleWithNeighbors, Architecture> answer = new LinkedHashMap<>();
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
			}
		}
		Set<DefaultEdge> maximumMatching = new HopcroftKarpBipartiteMatching<>(bigraph, partition1, partition2).getMatching();
		UndirectedGraph<Object, DefaultEdge> matchingToMutate = new SimpleGraph<>(edgeFactory);
		for (Object vertex : bigraph.vertexSet()) {
			matchingToMutate.addVertex(vertex);
		}
		for (DefaultEdge edge : maximumMatching) {
			matchingToMutate.addEdge(bigraph.getEdgeSource(edge), bigraph.getEdgeTarget(edge), edge);
		}

		UndirectedGraph<Object, DefaultEdge> generatedPerfectMatching = JerrumSinclairMarkovChain.inGraph(bigraph)
			.withInitialMatchingToMutate(matchingToMutate)
			.withNumberOfSteps(100)
			.withRandom(random);
		for (DefaultEdge edge : generatedPerfectMatching.edgeSet()) {
			if (generatedPerfectMatching.getEdgeSource(edge) instanceof Partition1Vertex) {
				ArchitecturePolicy policy = ((Partition1Vertex) generatedPerfectMatching.getEdgeSource(edge)).policy;
				RectangleWithNeighbors lot = (RectangleWithNeighbors) generatedPerfectMatching.getEdgeTarget(edge);
				answer.put(lot, architecture.get(policy));
			}
		}
		return answer;
	}

	private void initSortedLots(Map<ArchitecturePolicy, Set<RectangleWithNeighbors>> possiblePlaces) {
		for (RectangleWithNeighbors lot : lots) {
			policyConstrainedLots.put(lot, new LinkedList<>());
		}
		for (Map.Entry<ArchitecturePolicy, Set<RectangleWithNeighbors>> e : possiblePlaces.entrySet()) {
			ArchitecturePolicy policy = e.getKey();
			for (RectangleWithNeighbors lot : e.getValue()) {
				policyConstrainedLots.get(lot).add(policy);
			}
		}
		for (RectangleWithNeighbors lot : lots) {
			sortedLots.add(lot);
		}
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
	private Map<ArchitecturePolicy, Set<RectangleWithNeighbors>> findPossiblePlaces(
		Map<ArchitecturePolicy, Architecture> architectures,
		Set<RectangleWithNeighbors> allPlaces
	) {
		Map<ArchitecturePolicy, Set<RectangleWithNeighbors>> answer = new LinkedHashMap<>();
		for (Map.Entry<ArchitecturePolicy, Architecture> e : architectures.entrySet()) {
			Architecture architecture = e.getValue();
			ArchitecturePolicy policy = e.getKey();
			Set<RectangleWithNeighbors> places = new LinkedHashSet<>();
			answer.put(policy, places);
			Collection<Set<RectangleWithNeighbors>> setsByConstraints = new ArrayList<>(3);
			if (!policy.onStreet.isEmpty()) {
				for (Street street : policy.onStreet) {
					setsByConstraints.add(lotsTouchingStreets.getLotsOnStreet(street));
				}
			}
			if (policy.allowedArea != null) {
				Set<RectangleWithNeighbors> allowedArea = new LinkedHashSet<>();
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
				Iterator<Set<RectangleWithNeighbors>> iter = setsByConstraints.iterator();
				Set<RectangleWithNeighbors> allConstraintsApplied = iter.next();
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

	private class Partition1Vertex {
		private final ArchitecturePolicy policy;

		public Partition1Vertex(ArchitecturePolicy policy) {
			this.policy = policy;
		}
	}
}
