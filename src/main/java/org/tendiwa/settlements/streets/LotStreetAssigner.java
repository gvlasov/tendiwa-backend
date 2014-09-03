package org.tendiwa.settlements.streets;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.RectangleWithNeighbors;

import java.util.List;

@FunctionalInterface
/**
 * A strategy of determining what lot is on what street.
 */
public interface LotStreetAssigner {
	public List<Point2D> assignStreet(RectangleWithNeighbors buildingPlace);
}
