package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Placeable;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;
import org.tendiwa.settlements.streets.LotStreetAssigner;

import java.util.*;

public class CityBuilder {
	private final Map<RectangleWithNeighbors, Building> buildings = new HashMap<>();
	private String localizationId;
	private final Map<List<Point2D>, Street> streets = new IdentityHashMap<>();
	private final Set<Placeable> districts = new LinkedHashSet<>();
	private final Info info = new Info();

	CityBuilder() {

	}

	public void placeBuildings(BuildingPlacer placer) {
		placer.placeBuildings(info);
	}

	public void addLots(Collection<RectangleWithNeighbors> lots) {
		for (RectangleWithNeighbors lot : lots) {
			if (buildings.containsKey(lot)) {
				throw new IllegalArgumentException(
					"Building lot " + lot + " has already been added"
				);
			}
			this.buildings.put(lot, null);
		}
	}

	public void mapLotsToStreets(LotStreetAssigner mapper) {
		for (RectangleWithNeighbors lot : buildings.keySet()) {
			List<Point2D> street = mapper.assignStreet(lot);
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

	public class Info {
		private final Set<RectangleWithNeighbors> buildingPlaces = Collections.unmodifiableSet(
			CityBuilder.this.buildings.keySet()
		);
		private final Collection<Street> streets = Collections.unmodifiableCollection(
			CityBuilder.this.streets.values()
		);
		private final Set<Placeable> districts = Collections.unmodifiableSet(
			CityBuilder.this.districts
		);

		public Set<RectangleWithNeighbors> getBuildingPlaces() {
			return buildingPlaces;
		}

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

