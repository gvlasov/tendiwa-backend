package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Rectangle;

import java.util.function.Predicate;

/**
 * Unlike {@link City}, UrbanPlanner knows building places' relation to each other.
 */
public final class UrbanPlanner {
	private final City city;

	UrbanPlanner(City city) {
		this.city = city;
	}

	public void raiseBuilding(Rectangle where, Architecture what) {
		city.addBuildingPlace(where);
		BuildingFeatures features = new BuildingFeatures();
		features.setBuildingLot(where);
		features.setStreet(getClosestStreet(where));
		what.draw(features, );
		city.addBuilding(features.build());
	}

	private Street getClosestStreet(Rectangle lot) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks free building places for one that meets {@code condition}, and places a {@link Building} there it it is
	 * found.
	 *
	 * @param architecture
	 * 	Building type.
	 * @param condition
	 * 	A criteria for a {@link Rectangle} where {@code architecture} may be placed.
	 * @return true if a new {@link Building} is successfully placed, false otherwise.
	 */
	public boolean raiseWhereFits(Architecture architecture, Predicate<Rectangle> condition) {
		for (Rectangle rectangle : city.buildings.keySet()) {
			if (city.buildings.get(rectangle) == null && architecture.fits(rectangle) && condition.test(rectangle)) {
				raiseBuilding(rectangle, architecture);
				return true;
			}
		}
		return false;
	}

	public void addAvailableArchitecture(Architecture architecture, ArchitecturePolicy policy) {

	}
	/**
	 * Occupies all available places with new buildings, doesn't touch places that are already occupied.
	 */
	private void raiseAll() {
		for (Rectangle rectangle : city.buildings.keySet()) {
			if (city.isOccupied(rectangle)) {
				continue;
			}

		}

	}
}

