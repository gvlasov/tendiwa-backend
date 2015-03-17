package org.tendiwa.settlements.streets;

import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.geometry.Chain2D;

@FunctionalInterface
/**
 * A strategy of determining what lot is on what street.
 */
public interface LotStreetAssigner {
	public Chain2D getStreet(RectangleWithNeighbors buildingPlace);
}
