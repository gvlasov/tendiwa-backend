package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.HorizontalPlane;
import org.tendiwa.core.Location;
import org.tendiwa.settlements.streets.Street;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static java.util.stream.Collectors.toList;

/**
 * UrbanPlanner decides where to place individual {@link Building}s using {@link org.tendiwa.settlements.buildings
 * .Architecture} available to it.
 */
public final class UrbanPlanner implements BuildingPlacer {

	private final Map<ArchitecturePolicy, Architecture> architecture = new LinkedHashMap<>();
	private final HorizontalPlane plane;
	private final double streetsWidth;
	private final LotFacadeAssigner facadeAssigner;
	private final Random random;
	private PolylineProximity polylineProximity;


	public UrbanPlanner(HorizontalPlane plane, double streetsWidth, LotFacadeAssigner facadeAssigner, Random random) {
		this.plane = plane;
		this.streetsWidth = streetsWidth;
		this.facadeAssigner = facadeAssigner;
		this.random = random;
	}


	@Override
	public void placeBuildings(CityBuilder.Info cityInfo) {
		polylineProximity = new PolylineProximity(
			cityInfo.getStreets().stream().map(Street::getPoints).collect(toList()),
			cityInfo.getBuildingPlaces(),
			streetsWidth
		);
		Map<RectangleWithNeighbors, Architecture> placement = new UrbanPlanningStrategy(
			architecture,
			polylineProximity,
			cityInfo.getBuildingPlaces(),
			random
		).compute();
		for (RectangleWithNeighbors rectangle : placement.keySet()) {
			addBuilding(rectangle, placement.get(rectangle), cityInfo);
		}
	}

	public void addAvailableArchitecture(Architecture architecture, ArchitecturePolicy policy) {
		if (this.architecture.containsKey(policy)) {
			throw new IllegalArgumentException("This policy is already contained in this UrbanPlanner");
		}
		this.architecture.put(policy, architecture);
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
		CardinalDirection facadeDirection = facadeAssigner.getFacadeDirection(where);
		assert facadeDirection != null;
		what.draw(
			features,
			facadeDirection,
			new Location(plane, where.rectangle.x, where.rectangle.y, where.rectangle.width, where.rectangle.height)
		);
		info.addBuilding(features.build());
	}
}

