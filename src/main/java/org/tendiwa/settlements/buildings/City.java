package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.lexeme.Localizable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class City {
	private final Localizable name;
	final Map<Rectangle, Building> buildings = new HashMap<>();
	private final List<Street> streets = new LinkedList<>();

	public City(Localizable name) {
		this.name = name;
	}

	public void addBuildingPlace(Rectangle place) {
		if (buildings.containsKey(place)) {
			throw new IllegalArgumentException(
				"Building place " + place + " has already been added to city " + name.getLocalizationId()
			);
		}
		buildings.put(place, null);
	}

	void addBuilding(Building building) {
		assert buildings.containsKey(building.place);
		buildings.put(building.place, building);
	}

	void addStreet(List<Point2D> route, Localizable name) {
		streets.add(new Street(route, name));
	}

	public UrbanPlanner getBuildingPlacementManager() {
		return new UrbanPlanner(buildings, streets);
	}

	/**
	 * Checks if a place is already occupied by some building.
	 *
	 * @param rectangle
	 * 	A place.
	 * @return true if it is occupied, false if it is free or if it is not even a valid building place for this City.
	 */
	boolean isOccupied(Rectangle rectangle) {
		return buildings.get(rectangle) != null;
	}
}

