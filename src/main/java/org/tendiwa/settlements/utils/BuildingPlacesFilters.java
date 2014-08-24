package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.buildings.StreetAssigner;

import java.awt.Color;
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
		StreetAssigner streetAssigner = new StreetAssigner(roads, distance);
		return buildingPlace -> {
			streetAssigner.addBuilding(buildingPlace.rectangle);
			return streetAssigner.hasStreets(buildingPlace.rectangle);
		};
	}
}
