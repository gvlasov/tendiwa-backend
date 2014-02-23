package org.tendiwa.core.settlements;

import org.tendiwa.geometry.Cell;
import org.tendiwa.core.FloorType;
import org.tendiwa.core.meta.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class RoadSystem {
ArrayList<Road> roads = new ArrayList<Road>();
private HashMap<Road, ArrayList<Intersection>> intersections = new HashMap<Road, ArrayList<Intersection>>();
private HashMap<Road, ArrayList<Intersection>> branches = new HashMap<Road, ArrayList<Intersection>>();

public RoadSystem() {

}

public Road createRoad(int startX, int startY, int endX, int endY) {
	if (startX > endX) {
		int buf = startX;
		startX = endX;
		endX = buf;
	}
	if (startY > endY) {
		int buf = startY;
		startY = endY;
		endY = buf;
	}
	Road newRoad = new Road(startX, startY, endX, endY);

	// If new road lies on the same cells as one of
	// existing roads, then throw an error.
	if (newRoad.orientation.isVertical()) {
		for (Road road : roads) {
			if (road.orientation.isVertical() && road.start.getX() == newRoad.start.getX() && Utils.integersRangeIntersection(road.start.getY(), road.end.getY(), newRoad.start.getY(), newRoad.end.getY()) >= 1) {
				throw new Error("Two roads on the same vertical line: " + newRoad + " and " + road);
			}
		}
	} else {
		for (Road road : roads) {
			if (road.orientation.isVertical() && road.start.getY() == newRoad.start.getY() && Utils.integersRangeIntersection(road.start.getX(), road.end.getX(), newRoad.start.getX(), newRoad.end.getX()) >= 1) {
				throw new Error("Two roads on the same horizontal line: " + newRoad + " and " + road);
			}
		}
	}
	findChanges(newRoad);
	roads.add(newRoad);
	intersections.put(newRoad, new ArrayList<Intersection>());
	branches.put(newRoad, new ArrayList<Intersection>());

	return newRoad;
}

public void findChanges(Road newRoad) {
	/**
	 * Find new branches and intersections after adding newRoad.
	 */
	for (Road oldRoad : roads) {
		if (!areParallel(newRoad, oldRoad)) {
			// If roads are parallel, then go to next road
			if (newRoad.orientation.isVertical()) {
				// If new road is vertical
				if ((newRoad.start.getY() == oldRoad.start.getY() || newRoad.end.getY() == oldRoad.start.getY()) && newRoad.start.getX() >= oldRoad.start.getX() && newRoad.start.getX() <= oldRoad.end.getX()) {
					branches.get(oldRoad).add(new Intersection(newRoad, new Cell(newRoad.start.getX(), oldRoad.start.getY())));
				} else if (newRoad.start.getX() >= oldRoad.start.getX() && newRoad.start.getX() <= oldRoad.end.getX() && oldRoad.start.getY() >= oldRoad.start.getY() && newRoad.start.getY() <= oldRoad.end.getY()) {
					intersections.get(oldRoad).add(new Intersection(newRoad, new Cell(newRoad.start.getX(), oldRoad.start.getY())));
				}
			} else {
				// If new road is horizontal
				if ((newRoad.start.getX() == oldRoad.start.getX() || newRoad.end.getX() == oldRoad.start.getX()) && newRoad.start.getY() >= oldRoad.start.getY() && newRoad.start.getY() <= oldRoad.end.getY()) {
					branches.get(oldRoad).add(new Intersection(newRoad, new Cell(oldRoad.start.getX(), newRoad.start.getY())));
				} else if (newRoad.start.getY() >= oldRoad.start.getY() && newRoad.start.getY() <= oldRoad.end.getY() && oldRoad.start.getX() >= oldRoad.start.getX() && newRoad.start.getX() <= oldRoad.end.getX()) {
					intersections.get(oldRoad).add(new Intersection(newRoad, new Cell(oldRoad.start.getX(), newRoad.start.getY())));
				}
			}
		}
	}
}

public boolean areParallel(Road road1, Road road2) {
	return road1.orientation == road2.orientation;
}

public void drawRoads(FloorType floor) {
	for (Road road : roads) {
//		boldLine(road.start.x, road.start.y, road.end.x, road.end.y, floor, 5);
	}
}

public void printStatistics() {
	System.out.println("Roads: " + roads.size());
	int count = 0;
	for (ArrayList<Intersection> entity : branches.values()) {
		count += entity.size();
	}
	System.out.println("Branches: " + count);
	count = 0;
	for (ArrayList<Intersection> entity : intersections.values()) {
		count += entity.size();
	}
	System.out.println("Intersections: " + count);
}

public ArrayList<Cell> getReferencePoints() {
	/**
	 * Get significant points of road systems: intersections, starts of
	 * branches and ends of roads
	 */
	ArrayList<Cell> answer = new ArrayList<>();
	for (ArrayList<Intersection> list : intersections.values()) {
		// Intersections
		for (Intersection intersection : list) {
			answer.add(intersection.point);
		}
	}
	// Branches points will be added in roads loop (all branch points
	// are road starts/ends)
	for (Road road : roads) {
		// Ends of roads
		answer.add(road.start);
		answer.add(road.end);
	}
	return answer;
}

public class Intersection {
	/**
	 * Intersection or branch
	 */
	protected Road road;
	protected Cell point;

	public Intersection(Road road, Cell point) {
		this.road = road;
		this.point = point;
	}
}

}

