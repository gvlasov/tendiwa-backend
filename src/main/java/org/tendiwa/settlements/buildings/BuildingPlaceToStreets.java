package org.tendiwa.settlements.buildings;

import org.tendiwa.settlements.RectangleWithNeighbors;

@FunctionalInterface
public interface BuildingPlaceToStreets {
	public Street get(RectangleWithNeighbors buildingPlace);
}
