package org.tendiwa.settlements.buildings;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.RecTree;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;

import java.util.Map;
import java.util.Set;

public final class City {
	final Map<RectangleWithNeighbors, Building> lotsToBuildings;
	final Set<Street> streets;
	final Set<RecTree> districts;
	private final String localizationId;

	City(
		String localizationId,
		Map<RectangleWithNeighbors, Building> lotsToBuildings,
		ImmutableSet<Street> streets,
		Set<RecTree> districts
	) {
		this.localizationId = localizationId;
		this.lotsToBuildings = lotsToBuildings;
		this.streets = streets;
		this.districts = districts;
	}

	public static CityBuilder builder() {
		return new CityBuilder();
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

