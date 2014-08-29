package org.tendiwa.settlements.buildings;

@FunctionalInterface
public interface BuildingPlacer {
	/**
	 * Draws buildings' cell content inside a city described by {@code cityInfo}.
	 */
	public void placeBuildings(CityBuilder.Info cityInfo);
}
