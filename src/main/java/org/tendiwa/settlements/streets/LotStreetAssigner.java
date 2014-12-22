package org.tendiwa.settlements.streets;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;

import java.util.List;

@FunctionalInterface
/**
 * A strategy of determining what lot is on what street.
 */
public interface LotStreetAssigner {
	public List<Point2D> getStreet(RectangleWithNeighbors buildingPlace);
}
