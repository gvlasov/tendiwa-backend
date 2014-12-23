package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.buildings.PolylineProximity;

import java.util.Set;
import java.util.function.Predicate;

public class BuildingPlacesFilters {
	/**
	 * Filters building places that have access to streets
	 *
	 * @param streets
	 * 	Streets.
	 * @param lots
	 * 	Lots.
	 * @param distance
	 * 	How far away from a street should a lot be to have access to it.
	 * @return A {@link java.util.function.Predicate} that checks if a building has access to any streets.
	 */
	public static Predicate<RectangleWithNeighbors> closeToRoads(
		Set<ImmutableList<Point2D>> streets,
		Iterable<RectangleWithNeighbors> lots,
		double distance
	) {
		PolylineProximity polylineProximity = new PolylineProximity(streets, lots, distance);
		return polylineProximity::hasStreets;
	}
}
