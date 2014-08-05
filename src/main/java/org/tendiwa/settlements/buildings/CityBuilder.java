package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Placeable;
import org.tendiwa.geometry.Rectangle;

import java.util.*;
import java.util.function.Function;

public class CityBuilder {
	private final Map<Rectangle, Building> buildings = new HashMap<>();
	private String localiationId;
	private final Set<Street> streets = new LinkedHashSet<>();
	private final Set<Placeable> districts = new LinkedHashSet<>();
	private final Info info = new Info();

	public CityBuilder() {

	}

	public void placeBuildings(BuildingPlacer placer) {
		placer.placeBuildings(info);
	}

	public void addLots(Collection<Rectangle> lots) {
		for (Rectangle lot : lots) {
			if (buildings.containsKey(lot)) {
				throw new IllegalArgumentException(
					"Building lot " + lot + " has already been added"
				);
			}
			this.buildings.put(lot, null);
		}
	}

	public void mapLotsToStreets(Function<Rectangle, Street> mapper) {
		for (Rectangle lot : buildings.keySet()) {
			Street street = mapper.apply(lot);
			if (street == null) {
				throw new NullPointerException("Lot can't be mapped to Street null");
			}
			if (!streets.contains(street)) {
				streets.add(street);
			}
		}
	}

	public void setLocalizationId(String localizationId) {
		this.localiationId = localizationId;
	}

	public class Info {
		private final Set<Rectangle> buildingPlaces = Collections.unmodifiableSet(CityBuilder.this.buildings.keySet());
		private final Set<Street> streets = Collections.unmodifiableSet(CityBuilder.this.streets);
		private final Set<Placeable> districts = Collections.unmodifiableSet(CityBuilder.this.districts);

		public Set<Rectangle> getBuildingPlaces() {
			return buildingPlaces;
		}

		public Set<Street> getStreets() {
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

