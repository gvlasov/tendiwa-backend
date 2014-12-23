package org.tendiwa.settlements.buildings;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Placeable;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.LotStreetAssigner;
import org.tendiwa.settlements.streets.Street;

import java.util.*;

public class CityBuilder {
	private final Map<RectangleWithNeighbors, Building> buildings = new LinkedHashMap<>();
	// TODO: Keys are lists, linked hash map has O(n) hashing!
	private final Map<List<Point2D>, Street> streets = new LinkedHashMap<>();
	private final Set<Placeable> districts = new LinkedHashSet<>();
	private final Info info = new Info();
	private String localizationId;
	private boolean used = false;

	CityBuilder() {
	}

	public void placeBuildings(BuildingPlacer placer) {
		placer.placeBuildings(info);
	}

	public CityBuilder addLots(Collection<RectangleWithNeighbors> lots) {
		for (RectangleWithNeighbors lot : lots) {
			if (buildings.containsKey(lot)) {
				throw new IllegalArgumentException(
					"Building lot " + lot + " has already been added"
				);
			}
			this.buildings.put(lot, null);
		}
		return this;
	}

	public void mapLotsToStreets(LotStreetAssigner mapper) {
		for (RectangleWithNeighbors lot : buildings.keySet()) {
			List<Point2D> street = mapper.getStreet(lot);
			if (street == null) {
				throw new NullPointerException("Lot can't be mapped to null Street");
			}
			if (!streets.containsKey(street)) {
				streets.put(street, null);
			}
		}
	}

	public void setLocalizationId(String localizationId) {
		this.localizationId = localizationId;
	}

	/**
	 * Creates a new City object from this CityBuilder.
	 *
	 * @return New City.
	 * @throws java.lang.UnsupportedOperationException
	 * 	when this method is being used more than one time on the same
	 * 	CityBuilder.
	 */
	public City build() {
		if (used) {
			throw new UnsupportedOperationException("Trying to reuse CityBuilder");
		}
		used = true;
		return new City(localizationId, buildings, ImmutableSet.copyOf(streets.values()), districts);
	}

	public class Info {
		private final Set<RectangleWithNeighbors> buildingPlaces = Collections.unmodifiableSet(
			CityBuilder.this.buildings.keySet()
		);
		private final Collection<Street> streets = Collections.unmodifiableCollection(
			CityBuilder.this.streets.values()
		);

		public Set<RectangleWithNeighbors> getBuildingPlaces() {
			return buildingPlaces;
		}

		private final Set<Placeable> districts = Collections.unmodifiableSet(
			CityBuilder.this.districts
		);

		public Collection<Street> getStreets() {
			return streets;
		}

		public Set<Placeable> getDistricts() {
			return districts;
		}

		public void addBuilding(Building building) {
			CityBuilder.this.buildings.put(building.place, building);
		}


	}
}

