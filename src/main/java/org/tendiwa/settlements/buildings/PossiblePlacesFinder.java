package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.StupidPriceduralRecs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;

import java.util.*;

public class PossiblePlacesFinder {

	private final PolylineProximity polylineProximity;

	public PossiblePlacesFinder(PolylineProximity polylineProximity) {
		this.polylineProximity = polylineProximity;
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
	 * {@link org.tendiwa.settlements.buildings.Architecture#fits(org.tendiwa.settlements.utils.RectangleWithNeighbors)} )}.
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
	LinkedHashMap<ArchitecturePolicy, LinkedHashSet<RectangleWithNeighbors>> findPossiblePlaces(
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
					setsByConstraints.add(new LinkedHashSet<>(polylineProximity.getLotsOnStreet
						(street)));
				}
			}
			if (policy.allowedArea != null) {
				LinkedHashSet<RectangleWithNeighbors> allowedArea = new LinkedHashSet<>();
				Rectangle boundingRec = policy.allowedArea.bounds();
				for (RectangleWithNeighbors place : allPlaces) {
					Optional<Rectangle> intersection = boundingRec.intersectionWith(place.rectangle);
					if (intersection.isPresent() && intersection.get().equals(place.rectangle)) {
						allowedArea.add(place);
					}
				}
				Iterator<RectangleWithNeighbors> iter = allowedArea.iterator();
				while (iter.hasNext()) {
					RectangleWithNeighbors rectangle = iter.next();
					if (!StupidPriceduralRecs.placeableEnclosesRectangle(policy.allowedArea, rectangle.rectangle)) {
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
}