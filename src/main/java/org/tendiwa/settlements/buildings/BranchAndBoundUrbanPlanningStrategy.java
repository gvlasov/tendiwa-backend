package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;

import java.util.*;

/**
 * Finds a mapping from places to architecture that satisfies architecture policies.
 */
final class BranchAndBoundUrbanPlanningStrategy {
	private final Map<Architecture, ArchitecturePolicy> architecture;
	private final StreetAssigner streetAssigner;
	private final Set<Rectangle> places;

	/**
	 * @param architecture
	 * @param streetAssigner
	 * @param places
	 * @param random
	 * 	Seeded random.
	 */
	BranchAndBoundUrbanPlanningStrategy(
		Map<Architecture, ArchitecturePolicy> architecture,
		StreetAssigner streetAssigner,
		Set<Rectangle> places,
		Random random
	) {
		this.architecture = architecture;
		this.streetAssigner = streetAssigner;
		this.places = places;
	}

	public Map<Rectangle, Architecture> compute() {
		DependencyGraph dependencyGraph = new DependencyGraph(architecture);
		Map<Architecture, Set<Rectangle>> areaRestrictedPlaces = findPossiblePlaces(architecture, places);
		return null;
	}

	/**
	 * Searches for sets of places that is defined with the following constraints:
	 * <ul>
	 * <li>
	 * {@link org.tendiwa.settlements.buildings.ArchitecturePolicy#onStreet};
	 * </li>
	 * <li>
	 * {@link org.tendiwa.settlements.buildings.ArchitecturePolicy#allowedArea};
	 * </li>
	 * <li>
	 * {@link org.tendiwa.settlements.buildings.Architecture#fits(org.tendiwa.geometry.Rectangle)}.
	 * </li>
	 * </ul>
	 *
	 * @param architectures
	 * 	Architectures and their policies.
	 * @param allPlaces
	 * 	All available unoccupied places.
	 * @return A map from Architecture to a set of places where it can be placed based on "dependency-less"
	 * constraints.
	 */
	private Map<Architecture, Set<Rectangle>> findPossiblePlaces(
		Map<Architecture, ArchitecturePolicy> architectures,
		Set<Rectangle> allPlaces
	) {
		Map<Architecture, Set<Rectangle>> answer = new LinkedHashMap<>();
		for (Map.Entry<Architecture, ArchitecturePolicy> e : architectures.entrySet()) {
			Architecture architecture = e.getKey();
			ArchitecturePolicy policy = e.getValue();
			Set<Rectangle> places = new LinkedHashSet<>();
			answer.put(architecture, places);
			Collection<Set<Rectangle>> setsByConstraints = new ArrayList<>(3);
			if (!policy.onStreet.isEmpty()) {
				for (Street street : policy.onStreet) {
					setsByConstraints.add(streetAssigner.getPlacesOnStreet(street));
				}
			}
			if (policy.allowedArea != null) {
				Set<Rectangle> allowedArea = new LinkedHashSet<>();
				Rectangle boundingRec = policy.allowedArea.getBounds();
				for (Rectangle place : allPlaces) {
					Optional<Rectangle> intersection = boundingRec.intersectionWith(place);
					if (intersection.isPresent() && intersection.get().equals(place)) {
						allowedArea.add(place);
					}
				}
				Iterator<Rectangle> iter = allowedArea.iterator();
				while (iter.hasNext()) {
					Rectangle rectangle = iter.next();
					if (!Recs.placeableContainsRectangle(policy.allowedArea, rectangle)) {
						iter.remove();
					}
				}

				setsByConstraints.add(allowedArea);
			}
			// Leave only those sets that satisfy certain policy constraints (not all policy constraints,
			// only the simplest ones).
			if (setsByConstraints.isEmpty()) {
				answer.put(architecture, new LinkedHashSet<>(allPlaces));
			} else {
				Iterator<Set<Rectangle>> iter = setsByConstraints.iterator();
				Set<Rectangle> allConstraintsApplied = iter.next();
				while (iter.hasNext()) {
					Set<Rectangle> anotherConstraint = iter.next();
					allConstraintsApplied.retainAll(anotherConstraint);
				}
				answer.put(architecture, allConstraintsApplied);
			}
			Iterator<Rectangle> iter = answer.get(architecture).iterator();
			while (iter.hasNext()) {
				// Leave only those places where architecture fits
				Rectangle rec = iter.next();
				if (!architecture.fits(rec)) {
					iter.remove();
				}
			}
		}
		return answer;
	}
}
