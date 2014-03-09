package org.tendiwa.core.settlements;

import org.tendiwa.core.TerrainModifier;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.RecursivelySplitRectangleSystemFactory;

import java.util.ArrayList;
import java.util.HashSet;

public class Quarter extends Rectangle {
public final QuarterSystem system;
public final ArrayList<Road> closeRoads = new ArrayList<>();

public Quarter(QuarterSystem system, org.tendiwa.geometry.Rectangle rectangle) {
	super(rectangle);
	this.system = system;
	for (Road road : system.settlement.roadSystem.roads) {
					/* */// May fail for roads with even width
		if (road.isRectangleOverlapsRoad(rectangle)) {
			closeRoads.add(road);
		}
	}

	// Now we have a rectangle with border near roads' center line.
	// Then we narrow rectangle by roads, according to roads' width.

	for (Road road : system.settlement.roadSystem.roads) {
		if (!road.crossesRectangle(this)) {
			narrowRectangleByRoad(this, road);
		}
	}
}

public HashSet<BuildingPlace> getBuildingPlaces(int minWidth) {
	HashSet<BuildingPlace> answer = new HashSet<>();
	TerrainModifier modifier = system.settlement.getTerrainModifier(RecursivelySplitRectangleSystemFactory.create(getX(), getY(), getWidth(), getHeight(), minWidth, 1));
	RectangleSystem rs = modifier.getRectangleSystem();
	for (Rectangle r : rs.getRectangles()) {
		if (rs.isRectangleOuter(r)) {
			answer.add(new BuildingPlace(r, this));
		}
	}
	return answer;
}

private void narrowRectangleByRoad(Rectangle rec, Road road) {
	/**
	 * Change rectangle start and dimensions as if road would
	 * "bite off" a part of rectangle by road's width.
	 */
//	CardinalDirection side = road.getSideOfRectangle(rec);
//	if (side == CardinalDirection.N) {
//		int newY = Math.max(road.start.y + road.width / 2 + 1, rec.y);
//		if (newY != rec.y) {
//			rec.setBounds(rec.x, newY, rec.width, rec.height - (newY - road.start.y) + 1);
//		}
//	} else if (side == CardinalDirection.E) {
//		int newEndX = Math.min(rec.x + rec.width - 1, road.start.x - road.width / 2 - 1);
//		if (newEndX != rec.x + rec.width - 1) {
//			rec.setSize(newEndX - rec.x + 1, rec.height);
//		}
//	} else if (side == CardinalDirection.S) {
//		int newEndY = Math.min(rec.y + rec.height - 1, road.start.y - road.width / 2 - 1);
//		if (newEndY != rec.y + rec.height - 1) {
//			rec.setSize(rec.width, newEndY - rec.y + 1);
//		}
//	} else if (side == CardinalDirection.W) {
//		int newX = Math.max(road.start.x + road.width / 2 + 1, rec.x);
//		if (newX != rec.x) {
//			rec.setBounds(newX, rec.y, rec.width - (newX - road.start.x) + 1, rec.height);
//		}
//	}
}
}
