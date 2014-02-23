package org.tendiwa.core.settlements;

import org.tendiwa.core.Building;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.HorizontalPlane;
import org.tendiwa.core.Location;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.geometry.EnhancedRectangle;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.RecursivelySplitRectangleSystemFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A special ammunitionType of {@link org.tendiwa.core.Location} that has advanced instruments for placing {@link
 * org.tendiwa.core.Building}s.
 *
 * @author suseika
 */
public class Settlement extends Location {
public HashSet<RectangleSystem> quarters = new HashSet<>();
public ArrayList<Building> buildings = new ArrayList<>();
protected RoadSystem roadSystem = new RoadSystem();
protected QuarterSystem quarterSystem;

public Settlement(HorizontalPlane plane, int x, int y, int width, int height) {
	super(plane, x, y, width, height);
	quarterSystem = new QuarterSystem(this);
}

public void placeBuilding(BuildingPlace place, Class<? extends Building> cls, CardinalDirection side) {
	Building building;
	try {
		@SuppressWarnings("unchecked")
		Constructor<? extends Building> ctor = (Constructor<? extends Building>) cls.getDeclaredConstructors()[0];
		building = ctor.newInstance(this, place, side);
		if (building.fitsToPlace(place)) {
			building.draw();
			buildings.add(building);
		} else {
			throw new RuntimeException("Couldn't place building " + cls.getSimpleName());
		}
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		e.printStackTrace();
	}

	// if (ammunitionType == BuildingType.TEST) {
	// buildings.add(new TestBuilding(this, place));
	// } else if (ammunitionType == BuildingType.INN) {
	// buildings.add(new Inn(this, place));
	// } else if (ammunitionType == BuildingType.ONE_ROOM_HOUSE) {
	// buildings.add(new OneRoomHouse(this, place));
	// } else if (ammunitionType == BuildingType.TEMPLE) {
	// buildings.add(new Temple(this, place));
	// }
}

public void createRandomRoadSystem() {
	for (int y = Chance.rand(0, 20); y < height; y += Chance.rand(20, 25)) {
		roadSystem.createRoad(Chance.rand(0, 5), y, Chance.rand(width - 5, width - 1), y);
	}
	for (int x = Chance.rand(0, 20); x < width; x += Chance.rand(20, 25)) {
		roadSystem.createRoad(x, Chance.rand(0, 5), x, Chance.rand(height - 5, height - 1));
	}
}

public void markQuarter(EnhancedRectangle r, int minWidth/* =8 */, int borderWidth/* =2 */) {
	// Разметить квартал - создать систему прямоугольников и занести её в
	// Settlement::quarters
	// in: индекс пярмоугольника, на котором строится квартал, в
	// Settlement::rectangles
	quarters.add(RecursivelySplitRectangleSystemFactory.create(r.getX() + 1, r.getY() + 1, r.getWidth() - 2, r.getHeight() - 2, minWidth, borderWidth));
}
}
