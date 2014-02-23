package org.tendiwa.core.settlements;

import org.tendiwa.core.EnhancedPoint;
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
			if (road.orientation.isVertical() && road.start.x == newRoad.start.x && Utils.integersRangeIntersection(road.start.y, road.end.y, newRoad.start.y, newRoad.end.y) >= 1) {
				throw new Error("Two roads on the same vertical line: " + newRoad + " and " + road);
			}
		}
	} else {
		for (Road road : roads) {
			if (road.orientation.isVertical() && road.start.y == newRoad.start.y && Utils.integersRangeIntersection(road.start.x, road.end.x, newRoad.start.x, newRoad.end.x) >= 1) {
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
				if ((newRoad.start.y == oldRoad.start.y || newRoad.end.y == oldRoad.start.y) && newRoad.start.x >= oldRoad.start.x && newRoad.start.x <= oldRoad.end.x) {
					branches.get(oldRoad).add(new Intersection(newRoad, new EnhancedPoint(newRoad.start.x, oldRoad.start.y)));
				} else if (newRoad.start.x >= oldRoad.start.x && newRoad.start.x <= oldRoad.end.x && oldRoad.start.y >= oldRoad.start.y && newRoad.start.y <= oldRoad.end.y) {
					intersections.get(oldRoad).add(new Intersection(newRoad, new EnhancedPoint(newRoad.start.x, oldRoad.start.y)));
				}
			} else {
				// If new road is horizontal
				if ((newRoad.start.x == oldRoad.start.x || newRoad.end.x == oldRoad.start.x) && newRoad.start.y >= oldRoad.start.y && newRoad.start.y <= oldRoad.end.y) {
					branches.get(oldRoad).add(new Intersection(newRoad, new EnhancedPoint(oldRoad.start.x, newRoad.start.y)));
				} else if (newRoad.start.y >= oldRoad.start.y && newRoad.start.y <= oldRoad.end.y && oldRoad.start.x >= oldRoad.start.x && newRoad.start.x <= oldRoad.end.x) {
					intersections.get(oldRoad).add(new Intersection(newRoad, new EnhancedPoint(oldRoad.start.x, newRoad.start.y)));
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

public ArrayList<EnhancedPoint> getReferencePoints() {
	/**
	 * Get significant points of road systems: intersections, starts of
	 * branches and ends of roads
	 */
	ArrayList<EnhancedPoint> answer = new ArrayList<>();
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
	protected EnhancedPoint point;

	public Intersection(Road road, EnhancedPoint point) {
		this.road = road;
		this.point = point;
	}
}

}

