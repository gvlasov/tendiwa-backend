package org.tendiwa.core.terrain.settlements;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import org.tendiwa.core.Settlement;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.EnhancedRectangle;

/**
 * Space for placing a building. Each quarter will be divided into 
 * several of those after roads' and quarters' generation.
 */
public class BuildingPlace extends EnhancedRectangle {
	public static final long serialVersionUID = 83682932346L;

	public final HashSet<Settlement.RoadSystem.Road> closeRoads = new HashSet<Settlement.RoadSystem.Road>();
	public BuildingPlace(EnhancedRectangle rectangle, Settlement.QuarterSystem.Quarter quarter) {
		super(rectangle);
		for (Settlement.RoadSystem.Road road : quarter.closeRoads) {
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
	 * @return
	 */
	public Set<CardinalDirection> getSidesOfCloseRoads() {
		return new HashSet<CardinalDirection>();
	}
	
}
