package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.buildings.LotsTouchingStreets;

import java.util.Set;
import java.util.function.Predicate;

public class BuildingPlacesFilters {
	/**
	 * Filters building places that
	 *
	 * @param roads
	 * @param distance
	 * @return
	 */
	public static Predicate<RectangleWithNeighbors> closeToRoads(
		Set<ImmutableList<Point2D>> roads,
		double distance
	) {
		LotsTouchingStreets lotsTouchingStreets = new LotsTouchingStreets(roads, distance);
		return buildingPlace -> {
			lotsTouchingStreets.addLot(buildingPlace);
			return lotsTouchingStreets.hasStreets(buildingPlace);
		};
	}
}
