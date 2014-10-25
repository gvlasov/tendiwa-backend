package org.tendiwa.core.settlements;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.Rectangle;

import java.util.HashSet;
import java.util.Set;

/**
 * Space for placing a building. Each quarter will be divided into several of those after roads' and quarters'
 * generation.
 */
public class BuildingPlace extends Rectangle {
	public final HashSet<Road> closeRoads = new HashSet<>();

	public BuildingPlace(Rectangle rectangle, Quarter quarter) {
		super(rectangle);
		for (Road road : quarter.closeRoads) {
			if (road.isRectangleNearRoad(this)) {
				closeRoads.add(road);
			}
		}
	}

	public BuildingPlace(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	/**
	 * Returns what sides the close roads are from.
	 *
	 * @return
	 */
	public Set<CardinalDirection> getSidesOfCloseRoads() {
		return new HashSet<CardinalDirection>();
	}

}
