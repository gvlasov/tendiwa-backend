package org.tendiwa.core.settlements;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Rectangle;

import java.util.ArrayList;

public class QuarterSystem {
private static final char EMPTY = '.';
private static final char ROAD = '/';
private static final char QUARTER = '#';
public ArrayList<Quarter> quarters = new ArrayList<>();
public ArrayList<BuildingPlace> buildingPlaces = new ArrayList<>();
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

public void build(ArrayList<Cell> points) {
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
			for (int y = road.start.getY(); y <= road.end.getY(); y++) {
				grid[road.start.getX()][y] = ROAD;
			}
		} else {
			for (int x = road.start.getX(); x <= road.end.getX(); x++) {
				grid[x][road.start.getY()] = ROAD;
			}
		}
	}
	for (Cell point : points) {
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
private void findQuarter(Cell point, int dx, int dy) {
	assert Math.abs(dx) == 1 && Math.abs(dy) == 1;
	Cell cornerPoint = new Cell(point.getX() + dx, point.getY() + dy);
	if (cornerPoint.getX() < 0 || cornerPoint.getY() < 0 || cornerPoint.getX() >= settlement.getWidth() || cornerPoint.getY() >= settlement.getHeight()) {
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
			int x = cornerPoint.getX() + dx * quarterWidth;
			if (x >= 0 && x < settlement.getWidth()) {
				// If x is inside location
				for (int y = cornerPoint.getY(); y != cornerPoint.getY() + dy * quarterHeight; y += dy) {
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
			int y = cornerPoint.getY() + dy * quarterHeight;
			if (y >= 0 && y < settlement.getHeight()) {
				// If y is inside location
				for (int x = cornerPoint.getX(); x != cornerPoint.getX() + dx * quarterWidth; x += dx) {
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
		quarters.add(new Quarter(this, new Rectangle(dx == 1 ? cornerPoint.getX()
			: cornerPoint.getX() - quarterWidth + 1, dy == 1 ? cornerPoint.getY()
			: cornerPoint.getY() - quarterHeight + 1, quarterWidth, quarterHeight)));
	}
}
}
