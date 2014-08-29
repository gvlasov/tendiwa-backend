package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.settlements.RectangleWithNeighbors;

import java.util.*;

/**
 * Finds a mapping from places to architecture that satisfies architecture policies.
 */
final class BranchAndBoundUrbanPlanningStrategy {
	private final Map<Architecture, ArchitecturePolicy> architecture;
	private final BuildingsTouchingStreets buildingsTouchingStreets;
	private final Set<RectangleWithNeighbors> places;

	/**
	 * @param architecture
	 * @param buildingsTouchingStreets
	 * @param places
	 * @param random
	 * 	Seeded random.
	 */
	BranchAndBoundUrbanPlanningStrategy(
		Map<Architecture, ArchitecturePolicy> architecture,
		BuildingsTouchingStreets buildingsTouchingStreets,
		Set<RectangleWithNeighbors> places,
		Random random
	) {
		this.architecture = architecture;
		this.buildingsTouchingStreets = buildingsTouchingStreets;
		this.places = places;
	}

	public Map<RectangleWithNeighbors, Architecture> compute() {
		DependencyGraph dependencyGraph = new DependencyGraph(architecture);
		Map<Architecture, Set<RectangleWithNeighbors>> areaRestrictedPlaces = findPossiblePlaces(architecture, places);
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
	 * {@link org.tendiwa.settlements.buildings.Architecture#fits(org.tendiwa.settlements.RectangleWithNeighbors)} )}.
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
	private Map<Architecture, Set<RectangleWithNeighbors>> findPossiblePlaces(
		Map<Architecture, ArchitecturePolicy> architectures,
		Set<RectangleWithNeighbors> allPlaces
	) {
		Map<Architecture, Set<RectangleWithNeighbors>> answer = new LinkedHashMap<>();
		for (Map.Entry<Architecture, ArchitecturePolicy> e : architectures.entrySet()) {
			Architecture architecture = e.getKey();
			ArchitecturePolicy policy = e.getValue();
			Set<RectangleWithNeighbors> places = new LinkedHashSet<>();
			answer.put(architecture, places);
			Collection<Set<RectangleWithNeighbors>> setsByConstraints = new ArrayList<>(3);
			if (!policy.onStreet.isEmpty()) {
				for (Street street : policy.onStreet) {
					setsByConstraints.add(buildingsTouchingStreets.getPlacesOnStreet(street));
				}
			}
			if (policy.allowedArea != null) {
				Set<RectangleWithNeighbors> allowedArea = new LinkedHashSet<>();
				Rectangle boundingRec = policy.allowedArea.getBounds();
				for (RectangleWithNeighbors place : allPlaces) {
					Optional<Rectangle> intersection = boundingRec.intersectionWith(place.rectangle);
					if (intersection.isPresent() && intersection.get().equals(place)) {
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
				answer.put(architecture, new LinkedHashSet<>(allPlaces));
			} else {
				Iterator<Set<RectangleWithNeighbors>> iter = setsByConstraints.iterator();
				Set<RectangleWithNeighbors> allConstraintsApplied = iter.next();
				while (iter.hasNext()) {
					Set<RectangleWithNeighbors> anotherConstraint = iter.next();
					allConstraintsApplied.retainAll(anotherConstraint);
				}
				answer.put(architecture, allConstraintsApplied);
			}
			Iterator<RectangleWithNeighbors> iter = answer.get(architecture).iterator();
			while (iter.hasNext()) {
				// Leave only those places where architecture fits
				RectangleWithNeighbors rec = iter.next();
				if (!architecture.fits(rec)) {
					iter.remove();
				}
			}
		}
		return answer;
	}
}
