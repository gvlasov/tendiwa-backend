package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.HorizontalPlane;
import org.tendiwa.core.Location;
import org.tendiwa.settlements.RectangleWithNeighbors;
import org.tendiwa.settlements.streets.Street;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * UrbanPlanner decides where to place individual {@link Building}s using {@link org.tendiwa.settlements.buildings
 * .Architecture} available to it.
 */
public final class UrbanPlanner implements BuildingPlacer {

	private final Map<Architecture, ArchitecturePolicy> architecture = new HashMap<>();
	private final HorizontalPlane plane;
	private final double streetsWidth;
	private BuildingsTouchingStreets buildingsTouchingStreets;


	public UrbanPlanner(HorizontalPlane plane, double streetsWidth) {
		this.plane = plane;
		this.streetsWidth = streetsWidth;
	}


	@Override
	public void placeBuildings(CityBuilder.Info cityInfo) {
		buildingsTouchingStreets = new BuildingsTouchingStreets(
			cityInfo.getStreets().stream().map(Street::getPoints).collect(Collectors.toSet()),
			streetsWidth
		);
		Map<RectangleWithNeighbors, Architecture> placement = new BranchAndBoundUrbanPlanningStrategy(
			architecture,
			buildingsTouchingStreets,
			cityInfo.getBuildingPlaces(),
			new Random(0)
		).compute();
		for (RectangleWithNeighbors rectangle : placement.keySet()) {
			addBuilding(rectangle, placement.get(rectangle), cityInfo);
		}
	}

	public void addAvailableArchitecture(Architecture architecture, ArchitecturePolicy policy) {
		this.architecture.put(architecture, policy);
	}

	private void addBuilding(RectangleWithNeighbors where, Architecture what, CityBuilder.Info info) {
		if (!what.fits(where)) {
			throw new ArchitectureError(
				"Architecture " + what.getClass().getName() + " doesn't fit in rectangle " + where
			);
		}
		BuildingFeatures features = new BuildingFeatures();
		features.setPlace(where);
//		features.setStreet(
//			streetAssigner.getStreetsForLot(where).iterator().next()
//		);
		what.draw(
			features,
			CardinalDirection.S,
			new Location(plane, where.rectangle.x, where.rectangle.y, where.rectangle.width, where.rectangle.height)
		);
		info.addBuilding(features.build());
	}
}

