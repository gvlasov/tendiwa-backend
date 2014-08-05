package org.tendiwa.settlements.buildings;

@FunctionalInterface
public interface BuildingPlacer {
	public void placeBuildings(CityBuilder.Info cityInfo);

}
