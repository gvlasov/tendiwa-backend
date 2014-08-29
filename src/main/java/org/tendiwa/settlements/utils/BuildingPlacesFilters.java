package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.buildings.BuildingsTouchingStreets;

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
		BuildingsTouchingStreets buildingsTouchingStreets = new BuildingsTouchingStreets(roads, distance);
		return buildingPlace -> {
			buildingsTouchingStreets.addBuilding(buildingPlace);
			return buildingsTouchingStreets.hasStreets(buildingPlace);
		};
	}
}
