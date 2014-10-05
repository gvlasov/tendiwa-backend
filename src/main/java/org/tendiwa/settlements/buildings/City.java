package org.tendiwa.settlements.buildings;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Placeable;
import org.tendiwa.lexeme.Localizable;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;

import java.util.*;

public final class City {
	private final String localizationId;
	final Map<RectangleWithNeighbors, Building> lotsToBuildings;
	final Set<Street> streets;
	final Set<Placeable> districts;

	public static CityBuilder builder() {
		return new CityBuilder();
	}

	City(
		String localizationId,
		Map<RectangleWithNeighbors, Building> lotsToBuildings,
		ImmutableSet<Street> streets,
		Set<Placeable> districts
	) {
		this.localizationId = localizationId;
		this.lotsToBuildings = lotsToBuildings;
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
		return lotsToBuildings.get(buildingPlace) != null;
	}

}

