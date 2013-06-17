package tendiwa.core.terrain.settlements;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import tendiwa.core.meta.Side;
import tendiwa.core.terrain.settlements.Settlement.QuarterSystem.Quarter;
import tendiwa.core.terrain.settlements.Settlement.RoadSystem.Road;
import tendiwa.geometry.EnhancedRectangle;

/**
 * Space for placing a building. Each quarter will be divided into 
 * several of those after roads' and quarters' generation.
 */
public class BuildingPlace extends EnhancedRectangle {
	public static final long serialVersionUID = 83682932346L;

	public final HashSet<Road> closeRoads = new HashSet<Road>();
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
	 * @return
	 */
	public Set<Side> getSidesOfCloseRoads() {
		return new HashSet<Side>();
	}
	
}
