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
			StreetAssigner sas = streetAssigner;
			streetAssigner.addBuilding(buildingPlace.rectangle);
			boolean b = streetAssigner.hasStreets(buildingPlace.rectangle);
			if (b) {
				TestCanvas.canvas.draw(buildingPlace.rectangle, DrawingRectangle.withColor(Color.red));
				System.out.println(1);
			}
			return b;
		};
	}
}
