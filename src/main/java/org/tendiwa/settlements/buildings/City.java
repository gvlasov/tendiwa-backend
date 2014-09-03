package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Placeable;
import org.tendiwa.lexeme.Localizable;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;

import java.util.*;

public final class City {
	private final Localizable name;
	final Map<RectangleWithNeighbors, Building> buildings;
	final Set<Street> streets;
	final Set<Placeable> districts;
	public static CityBuilder builder() {
		return new CityBuilder();
	}

	City(Localizable name, Map<RectangleWithNeighbors, Building> buildings, Set<Street> streets, Set<Placeable> districts) {
		this.name = name;
		this.buildings = buildings;
		this.streets = streets;
		this.districts = districts;
	}


	/**
	 * Checks if a place is already occupied by some building.
	 *
	 * @param buildingPlace
	 * 	A place for a building.
	 * @return true if it is occupied, false if it is free or if it is not even a valid building place for this City.
	 */
	boolean isOccupied(RectangleWithNeighbors buildingPlace) {
		return buildings.get(buildingPlace) != null;
	}

}

