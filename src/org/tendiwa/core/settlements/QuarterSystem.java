package org.tendiwa.core.settlements;

import org.tendiwa.core.EnhancedPoint;
import org.tendiwa.geometry.EnhancedRectangle;

import java.util.ArrayList;

public class QuarterSystem {
private static final char EMPTY = '.';
private static final char ROAD = '/';
private static final char QUARTER = '#';
public ArrayList<Quarter> quarters = new ArrayList<Quarter>();
public ArrayList<BuildingPlace> buildingPlaces = new ArrayList<BuildingPlace>();
Settlement settlement;
private char[][] grid;

public QuarterSystem(Settlement settlement) {
	this.settlement = settlement;
}

public void showGrid() {
	for (int y = 0; y < settlement.getWidth(); y++) {
		for (int x = 0; x < settlement.getHeight(); x++) {
			System.out.print(grid[x][y]);
		}
		System.out.println();
	}
}

public void build(ArrayList<EnhancedPoint> points) {
	/**
	 * Builds quarter system from significant points of road system
	 */
	// Grid will be filled with values that show what is in this cell:
	// road, quarter or nothing
	grid = new char[settlement.getWidth()][settlement.getHeight()];
	for (int i = 0; i < settlement.getWidth(); i++) {
		for (int j = 0; j < settlement.getHeight(); j++) {
			grid[i][j] = EMPTY;
		}
	}
	for (Road road : settlement.roadSystem.roads) {
		if (road.orientation.isVertical()) {
			for (int y = road.start.y; y <= road.end.y; y++) {
				grid[road.start.x][y] = ROAD;
			}
		} else {
			for (int x = road.start.x; x <= road.end.x; x++) {
				grid[x][road.start.y] = ROAD;
			}
		}
	}
	for (EnhancedPoint point : points) {
		findQuarter(point, 1, 1);
		findQuarter(point, 1, -1);
		findQuarter(point, -1, 1);
		findQuarter(point, -1, -1);
	}
	for (Quarter quarter : quarters) {
		for (BuildingPlace place : quarter.getBuildingPlaces(25)) {
			buildingPlaces.add(place);
		}
	}
}

/**
 * Find empty area from the particular side of point. SideTest is determined by dx and dy, both of which can be either 1
 * or -1. We expand the quarter until it stumbles upon a road, border of location or another quarter.
 */
private void findQuarter(EnhancedPoint point, int dx, int dy) {
	assert Math.abs(dx) == 1 && Math.abs(dy) == 1;
	EnhancedPoint cornerPoint = new EnhancedPoint(point.x + dx, point.y + dy);
	if (cornerPoint.x < 0 || cornerPoint.y < 0 || cornerPoint.x >= settlement.getWidth() || cornerPoint.y >= settlement.getHeight()) {
		return;
	}
	// grid[cornerPoint.x][cornerPoint.y] = QUARTER;
	boolean xStop = false;
	boolean yStop = false;
	int quarterWidth = 0;
	int quarterHeight = 1;    // This is necessary, because
	// after width becomes 1, height also
	// becomes 1
	for (int step = 0; !(xStop && yStop); step++) {
		if (!xStop) {
			int x = cornerPoint.x + dx * quarterWidth;
			if (x >= 0 && x < settlement.getWidth()) {
				// If x is inside location
				for (int y = cornerPoint.y; y != cornerPoint.y + dy * quarterHeight; y += dy) {
					if (grid[x][y] != EMPTY) {
						xStop = true;
						break;
					} else {
						grid[x][y] = QUARTER;
					}
				}
				if (!xStop) {
					quarterWidth++;
				}
			} else {
				xStop = true;
			}
		}
		if (!yStop) {
			int y = cornerPoint.y + dy * quarterHeight;
			if (y >= 0 && y < settlement.getHeight()) {
				// If y is inside location
				for (int x = cornerPoint.x; x != cornerPoint.x + dx * quarterWidth; x += dx) {
					if (x == settlement.getWidth() || x == -1) {
						yStop = true;
						break;
					} else if (grid[x][y] != EMPTY) {
						yStop = true;
						break;
					} else {
						grid[x][y] = QUARTER;
					}
				}
				if (!yStop) {
					quarterHeight++;
				}
			} else {
				yStop = true;
			}
		}
	}
	if (quarterWidth > 3 && quarterHeight > 3) {
		quarters.add(new Quarter(this, new EnhancedRectangle(dx == 1 ? cornerPoint.x
			: cornerPoint.x - quarterWidth + 1, dy == 1 ? cornerPoint.y
			: cornerPoint.y - quarterHeight + 1, quarterWidth, quarterHeight)));
	}
}
}
