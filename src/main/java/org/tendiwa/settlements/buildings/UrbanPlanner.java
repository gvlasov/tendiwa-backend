package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.HorizontalPlane;
import org.tendiwa.core.Location;
import org.tendiwa.geometry.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * UrbanPlanner decides where to place individual {@link Building}s using {@link org.tendiwa.settlements.buildings
 * .Architecture} available to it.
 */
public final class UrbanPlanner implements BuildingPlacer {

	private final Map<Architecture, ArchitecturePolicy> architecture = new HashMap<>();
	private final HorizontalPlane plane;
	private final double streetsWidth;
	private StreetAssigner streetAssigner;


	UrbanPlanner(HorizontalPlane plane, double streetsWidth) {
		this.plane = plane;
		this.streetsWidth = streetsWidth;
	}


	@Override
	public void placeBuildings(CityBuilder.Info cityInfo) {
		streetAssigner = new StreetAssigner(cityInfo.getBuildingPlaces(), cityInfo.getStreets(), streetsWidth);
		Map<Rectangle, Architecture> placement = new BranchAndBoundUrbanPlanningStrategy(
			architecture,
			streetAssigner,
			cityInfo.getBuildingPlaces(),
			new Random(0)
		).compute();
		for (Rectangle rectangle : placement.keySet()) {
			addBuilding(rectangle, placement.get(rectangle), cityInfo);
		}

	}

	public void addAvailableArchitecture(Architecture architecture, ArchitecturePolicy policy) {
		this.architecture.put(architecture, policy);
	}

	private void addBuilding(Rectangle where, Architecture what, CityBuilder.Info info) {
		if (!what.fits(where)) {
			throw new ArchitectureError(
				"Architecture " + what.getClass().getName() + " doesn't fit in rectangle " + where
			);
		}
		BuildingFeatures features = new BuildingFeatures();
		features.setPlace(where);
		features.setStreet(
			streetAssigner.getStreetsForBuildingPlace(where).iterator().next()
		);
		what.draw(
			features,
			CardinalDirection.S,
			new Location(plane, where.x, where.y, where.width, where.height)
		);
		info.addBuilding(features.build());
	}
}

