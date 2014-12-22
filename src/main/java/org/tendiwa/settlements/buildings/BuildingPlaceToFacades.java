package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;

@FunctionalInterface
public interface BuildingPlaceToFacades {
	public CardinalDirection get(RectangleWithNeighbors buildingPlace);
}
