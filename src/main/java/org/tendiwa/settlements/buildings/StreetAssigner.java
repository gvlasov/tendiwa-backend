package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.RectangleWithNeighbors;

import java.util.List;

@FunctionalInterface
public interface StreetAssigner {
	public List<Point2D> apply(RectangleWithNeighbors buildingPlace);
}
