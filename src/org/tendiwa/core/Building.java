package org.tendiwa.core;

import org.jgrapht.Graph;
import org.tendiwa.core.meta.Coordinate;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.core.settlements.Road;
import org.tendiwa.core.settlements.BuildingPlace;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.RecursivelySplitRectangleSystemFactory;

import java.util.*;

public abstract class Building {
private static HashMap<String, Class<? extends Building>> buildingClasses;
protected final int x;
protected final int y;
protected final int width;
protected final int height;
protected final CardinalDirection frontSide;
protected final CardinalDirection leftSide;
protected final CardinalDirection rightSide;
protected final CardinalDirection backSide;
protected Location settlement;
protected Collection<org.tendiwa.geometry.Rectangle> rooms;
protected TerrainModifier terrainModifier;
protected org.tendiwa.geometry.Rectangle lobby;
protected ArrayList<CardinalDirection> doorSides = new ArrayList<>();
protected Cell frontDoor;
/**
 * ArrayList of rectangleIds
 */
private ArrayList<Integer> hallways = new ArrayList<>();

protected Building(BuildingPlace bp, CardinalDirection direction) {
	x = bp.getX();
	y = bp.getY();
	width = bp.getWidth();
	height = bp.getHeight();
	frontSide = direction;
	leftSide = direction.clockwiseQuarter();
	rightSide = direction.counterClockwiseQuarter();
	backSide = direction.opposite();
}

/**
 * Sets the main properties of Building. This method must have been the class'es constructor, but this would require
 * each subclass of building to have an explicit constructor with the same signature.
 *
 * @param location
 * 	The {@link Location} in which the Building is built.
 * @param place
 * 	Where exactly in location the Building is built.
 * @param frontSide
 * 	From which cardinal side is the front door.
 */
public Building(Location location, BuildingPlace place, CardinalDirection frontSide) {
	this.x = place.getX() + 1;
	this.y = place.getY() + 1;
	this.width = place.getWidth() - 2;
	this.height = place.getHeight() - 2;
	this.settlement = location;
	for (Road road : place.closeRoads) {
		CardinalDirection side = road.getSideOfRectangle(new java.awt.Rectangle(x, y, width, height));
		if (!doorSides.contains(side)) {
			doorSides.add(side);
		}
	}
	// frontSide = doorSides.get(0);
	this.frontSide = frontSide;
	leftSide = frontSide.clockwiseQuarter();
	rightSide = frontSide.counterClockwiseQuarter();
	backSide = frontSide.opposite();
}

public static boolean registerClass(Class<? extends Building> cls) {
	buildingClasses.put(cls.getName(), cls);
	return true;
}

public CardinalDirection getDoorSide() {
	/**
	 * Returns first available door side
	 */
	if (doorSides.size() > 0) {
		return doorSides.get(0);
	} else {
		throw new Error("No available door sides");
	}
}

public Cell placeDoor(org.tendiwa.geometry.Rectangle r, CardinalDirection side, ObjectType object) {
	/**
	 * Places door in the middle of particular side of room.
	 */
	Cell c = r.getMiddleOfSide(side).moveToSide(side, 1);
	EntityPlacer.place(settlement.getActivePlane(), object, c.getX(), c.getY());
	return c;
}

public Cell placeDoor(org.tendiwa.geometry.Rectangle r, CardinalDirection side, CardinalDirection endOfSide, int depth, ObjectType door) {
	/**
	 * Places door in the particular cell on particular side of room
	 */
	Cell c = r.getCellFromSide(side, endOfSide, depth).moveToSide(side, 1);
	EntityPlacer.place(settlement.getActivePlane(), door, c.getX(), c.getY());
	return c;
}

public Cell placeFrontDoor(CardinalDirection side, ObjectType door) {
	HashMap<Integer, Integer> cells = findDoorAppropriateCells(side);
	if (cells.size() == 0) {
		throw new Error("Nowhere to place the door from side " + side);
	}
	int dx, dy;
	if (side == CardinalDirection.N || side == CardinalDirection.S) {
		ArrayList<Integer> xes = new ArrayList<>(cells.keySet());
		dx = xes.get(Chance.rand(0, xes.size() - 1));
		dy = cells.get(dx);
		EntityPlacer.place(settlement.getActivePlane(), door, dx, dy);
	} else {
		ArrayList<Integer> yes = new ArrayList<>(cells.keySet());
		dy = yes.get(Chance.rand(0, yes.size() - 1));
		dx = cells.get(dy);
		EntityPlacer.place(settlement.getActivePlane(), door, dx, dy);
	}
	switch (side) {
		case N:
			lobby = terrainModifier.getRectangleSystem().findRectangleByCell(dx, dy + 1);
			break;
		case E:
			lobby = terrainModifier.getRectangleSystem().findRectangleByCell(dx - 1, dy);
			break;
		case S:
			lobby = terrainModifier.getRectangleSystem().findRectangleByCell(dx, dy - 1);
			break;
		case W:
			lobby = terrainModifier.getRectangleSystem().findRectangleByCell(dx + 1, dy);
			break;
		default:
			throw new IllegalArgumentException();

	}
	if (lobby == null) {
		throw new Error("Can't determine the lobby room because desired cell is not in this rectangle system");
	}
	frontDoor = new Cell(dx, dy);
	return frontDoor;
}

public Cell placeFrontDoor(org.tendiwa.geometry.Rectangle r, CardinalDirection side, ObjectType door) {
	/**
	 * Place front door in the middle of rectangle from particular side.
	 */
	Cell doorCoord = r.getMiddleOfSide(side).moveToSide(side, 1);

	EntityPlacer.place(settlement.getActivePlane(), door, doorCoord.getX(), doorCoord.getY());
	lobby = r;
	frontDoor = doorCoord;
	return frontDoor;
}

public HashMap<Integer, Integer> findDoorAppropriateCells(CardinalDirection side) {
	HashMap<Integer, Integer> cells = new HashMap<>();
	Set<Integer> keys;
	if (side == CardinalDirection.N) {
		for (org.tendiwa.geometry.Rectangle r : rooms) {
			int y = r.getY() - 1;
			for (int i = r.getX(); i < r.getX() + r.getWidth(); i++) {
				if (!cells.containsKey(i) || cells.get(i) > y) {
					cells.put(i, y);
				}
			}
		}
		keys = new HashSet<>(cells.keySet());
		for (int x : keys) {
			int y = cells.get(x);
			if (settlement.getActivePlane().getPassability(x, y + 1) != Passability.FREE) {
				cells.remove(x);
			}
		}
	} else if (side == CardinalDirection.E) {
		for (org.tendiwa.geometry.Rectangle r : rooms) {
			int x = r.getX() + r.getWidth();
			for (int i = r.getY(); i < r.getY() + r.getHeight(); i++) {
				if (!cells.containsKey(i) || cells.get(i) < x) {
					cells.put(i, x);
				}
			}
		}
		keys = new HashSet<Integer>(cells.keySet());
		for (int y : keys) {
			int x = cells.get(y);
			if (settlement.getActivePlane().getPassability(x - 1, y) != Passability.FREE) {
				cells.remove(y);
			}
		}
	} else if (side == CardinalDirection.S) {
		for (org.tendiwa.geometry.Rectangle r : rooms) {
			int y = r.getY() + r.getHeight();
			for (int i = r.getX(); i < r.getX() + r.getWidth(); i++) {
				if (!cells.containsKey(i) || cells.get(i) < y) {
					cells.put(i, y);
				}
			}
		}
		keys = new HashSet<>(cells.keySet());
		for (int x : keys) {
			int y = cells.get(x);
			if (settlement.getActivePlane().getPassability(x, y - 1) != Passability.FREE) {
				cells.remove(x);
			}
		}
	} else if (side == CardinalDirection.W) {
		for (org.tendiwa.geometry.Rectangle r : rooms) {
			int x = r.getX() - 1;
			for (int i = r.getY(); i < r.getY() + r.getHeight(); i++) {
				if (!cells.containsKey(i) || cells.get(i) > x) {
					cells.put(i, x);
				}
			}
		}
		keys = new HashSet<Integer>(cells.keySet());
		for (int y : keys) {
			int x = cells.get(y);
			if (settlement.getActivePlane().getPassability(x + 1, y) != Passability.FREE) {
				cells.remove(y);
			}
		}
	}
	return cells;
}

public HashMap<Integer, Integer> findDoorAppropriateCells(java.awt.Rectangle r, CardinalDirection side) {
	HashMap<Integer, Integer> cells = new HashMap<>();
	Set<Integer> keys;
	if (side == CardinalDirection.N) {
		int y = r.y - 1;
		for (int i = r.x; i < r.x + r.width; i++) {
			if (!cells.containsKey(i) || cells.get(i) > y) {
				cells.put(i, y);
			}
		}
		keys = new HashSet<>(cells.keySet());
		for (int x : keys) {
			y = cells.get(x);
			if (settlement.getActivePlane().getPassability(x, y + 1) != Passability.FREE) {
				cells.remove(x);
			}
		}
	} else if (side == CardinalDirection.E) {
		int x = r.x + r.width;
		for (int i = r.y; i < r.y + r.height; i++) {
			if (!cells.containsKey(i) || cells.get(i) < x) {
				cells.put(i, x);
			}
		}
		keys = new HashSet<>(cells.keySet());
		for (int y : keys) {
			x = cells.get(y);
			if (settlement.getActivePlane().getPassability(x - 1, y) != Passability.FREE) {
				cells.remove(y);
			}
		}
	} else if (side == CardinalDirection.S) {
		int y = r.y + r.height;
		for (int i = r.x; i < r.x + r.width; i++) {
			if (!cells.containsKey(i) || cells.get(i) < y) {
				cells.put(i, y);
			}
		}
		keys = new HashSet<>(cells.keySet());
		for (int x : keys) {
			y = cells.get(x);
			if (settlement.getActivePlane().getPassability(x, y - 1) != Passability.FREE) {
				cells.remove(x);
			}
		}
	} else if (side == CardinalDirection.W) {
		int x = r.x - 1;
		for (int i = r.y; i < r.y + r.height; i++) {
			if (!cells.containsKey(i) || cells.get(i) > x) {
				cells.put(i, x);
			}
		}
		keys = new HashSet<Integer>(cells.keySet());
		for (int y : keys) {
			x = cells.get(y);
			if (settlement.getActivePlane().getPassability(x + 1, y) != Passability.FREE) {
				cells.remove(y);
			}
		}
	}
	return cells;
}

public TerrainModifier buildBasis(FloorType floor, ObjectType walls, ObjectType door) {
	TerrainModifier modifier = terrainModifier;
	RectangleSystem rs = modifier.getRectangleSystem();
	// if (notSimpleForm) {
	// if (graph.rectangles.size() > 3) {
	// // graph.initialFindOuterSides();
	// boolean formChanged = false;
	// Set<Integer> keys = graph.outerSides.keySet();
	// for (int k : keys) {
	// ArrayList<Integer> sides = graph.outerSides.get(k);
	// if (sides.size() == 0 || Chance.roll(70)) {
	// continue;
	// }
	// if (!graph.isVertexExclusible(k)) {
	// continue;
	// } else {
	// formChanged = true;
	// graph.excludeRectangle(k);
	// }
	// }
	// if (!formChanged) {
	// graph.excludeRectangle(0);
	// }
	// }
	// }
	// if (setup == BasisBuildingSetup.CONVERT_TO_DIRECTED_TREE) {
	// graph.convertGraphToDirectedTree();
	// } else if (setup == BasisBuildingSetup.KEYPOINTS_BASED) {
	//
	// } else if (setup == BasisBuildingSetup.NOT_BUILD_EDGES) {
	//
	// }

	modifier.drawInnerBorders(walls);
	for (org.tendiwa.geometry.Rectangle r : rs.getRectangles()) {
		fillFloor(r, floor);
	}
	Graph<org.tendiwa.geometry.Rectangle, RectangleSystem.Neighborship> graph = rs.getGraph();

	for (RectangleSystem.Neighborship e : graph.edgeSet()) {
		Coordinate c = connectRoomsWithDoor(graph.getEdgeSource(e), graph.getEdgeTarget(e), door);
		EntityPlacer.place(settlement.getActivePlane(), floor, c.x, c.y);
	}
	rooms = rs.getRectangles();
	return modifier;
}

public TerrainModifier getTerrainModifier(int minRoomSize) {
	return settlement.getTerrainModifier(RecursivelySplitRectangleSystemFactory.create(x, y, width, height, minRoomSize, 1));
}

public TerrainModifier setTerrainModifier(RectangleSystem crs) {
	return settlement.getTerrainModifier(crs);
}

protected Coordinate connectRoomsWithDoor(org.tendiwa.geometry.Rectangle r1, org.tendiwa.geometry.Rectangle r2, ObjectType door) {
	int x, y;
	if (r1.getX() + r1.getWidth() + 1 == r2.getX() || r2.getX() + r2.getWidth() + 1 == r1.getX()) {
		// Vertical
		x = Math.max(r1.getX() - 1, r2.getX() - 1);
		y = Chance.rand(Math.max(r1.getY(), r2.getY()), Math.min(r1.getY() + r1.getHeight() - 1, r2.getY() + r2.getHeight() - 1));
	} else {
		// Horizontal
		y = Math.max(r1.getY() - 1, r2.getY() - 1); // ��, ��� x ������ max, � �����
		// y - min.
		x = Chance.rand(Math.max(r1.getX(), r2.getX()), Math.min(r1.getX() + r1.getWidth() - 1, r2.getX() + r2.getWidth() - 1));
	}
	EntityPlacer.place(settlement.getActivePlane(), door, x, y);
	return new Coordinate(x, y);
}

protected void fillFloor(org.tendiwa.geometry.Rectangle r, FloorType floor) {
	settlement.square(r.getX(), r.getY(), r.getWidth(), r.getHeight(), floor, true);
}

private boolean isDoor(int x, int y) {
//	GameObject gameObject = settlement.getActivePlane().getGameObject(x, y);
//	return gameObject != null && gameObject.getType().getResourceName().equals("door");
	throw new UnsupportedOperationException();
}

protected ArrayList<Coordinate> getCellsNearWalls(java.awt.Rectangle r) {
	ArrayList<Coordinate> answer = new ArrayList<>();
	for (int i = r.x + 1; i < r.x + r.width - 1; i++) {
		if (!isDoor(i, r.y - 1)) {
			answer.add(new Coordinate(i, r.y));
		}
		if (!isDoor(i, r.y + r.height)) {
			answer.add(new Coordinate(i, r.y + r.height - 1));
		}
	}
	for (int i = r.y + 1; i < r.y + r.height - 1; i++) {
		if (!isDoor(r.x - 1, i)) {
			answer.add(new Coordinate(r.x, i));
		}
		if (!isDoor(r.x + r.width, i)) {
			answer.add(new Coordinate(r.x + r.width - 1, i));
		}
	}
	// Checking cells in corners
	if (!isDoor(r.x, r.y - 1) && !isDoor(r.x - 1, r.y)) {
		answer.add(new Coordinate(r.x, r.y));
	}
	if (!isDoor(r.x + r.width - 1, r.y - 1) && !isDoor(r.x + r.width, r.y)) {
		answer.add(new Coordinate(r.x + r.width - 1, r.y));
	}
	if (!isDoor(r.x + r.width, r.y + r.height - 1) && !isDoor(r.x + r.width - 1, r.y + r.height)) {
		answer.add(new Coordinate(r.x + r.width - 1, r.y + r.height - 1));
	}
	if (!isDoor(r.x, r.y + r.height) && !isDoor(r.x - 1, r.y + r.height - 1)) {
		answer.add(new Coordinate(r.x, r.y + r.height - 1));
	}
	return answer;
}

public ArrayList<Coordinate> getCellsNearDoors(java.awt.Rectangle r) {
	ArrayList<Coordinate> answer = new ArrayList<>();
	for (int i = r.x + 1; i < r.x + r.width - 1; i++) {
		if (isDoor(i, r.y - 1)) {
			answer.add(new Coordinate(i, r.y));
		}
		if (isDoor(i, r.y + r.height)) {
			answer.add(new Coordinate(i, r.y + r.height - 1));
		}
	}
	for (int i = r.y + 1; i < r.y + r.height - 1; i++) {
		if (isDoor(r.x - 1, i)) {
			answer.add(new Coordinate(r.x, i));
		}
		if (isDoor(r.x + r.width, i)) {
			answer.add(new Coordinate(r.x + r.width - 1, i));
		}
	}
	// Checking cells in corners
	if (isDoor(r.x, r.y - 1) || isDoor(r.x - 1, r.y)) {
		answer.add(new Coordinate(r.x, r.y));
	}
	if (isDoor(r.x + r.width - 1, r.y - 1) || isDoor(r.x + r.width, r.y)) {
		answer.add(new Coordinate(r.x + r.width - 1, r.y));
	}
	if (isDoor(r.x + r.width, r.y + r.height - 1) || isDoor(r.x + r.width - 1, r.y + r.height)) {
		answer.add(new Coordinate(r.x + r.width - 1, r.y + r.height - 1));
	}
	if (isDoor(r.x, r.y + r.height) || isDoor(r.x - 1, r.y + r.height - 1)) {
		answer.add(new Coordinate(r.x, r.y + r.height - 1));
	}
	return answer;
}

/**
 * Mark room as hallway so other rooms will prefer to connect to this room when buildBasis is called
 */
public void markAsHallway(int rectangleId) {
	hallways.add(rectangleId);
}

/**
 * Remove all the objects inside rooms.
 */
public void clearBasisInside() {
	for (org.tendiwa.geometry.Rectangle r : terrainModifier.getRectangleSystem().getRectangles()) {
		settlement.square(r.getX(), r.getY(), r.getWidth(), r.getHeight(), EntityPlacer.OBJECT_VOID, true);
	}
}

public void setLobby(org.tendiwa.geometry.Rectangle r) {
	lobby = r;
}

public void removeWall(java.awt.Rectangle r, CardinalDirection side) {
	/**
	 * Removes objects on rectangle's border from side side. Removed objects
	 * are not inside the rectangle, they are outside the rectangle. The
	 * ends of border are remain unremoved. Don't mix this up with
	 * TerrainGenerator.fillSideOfRectangle: that function removes objects
	 * inside the rectangle
	 */
	int startX, startY, endX, endY;
	switch (side) {
		case N:
			startX = r.x;
			startY = r.y - 1;
			endX = r.x + r.width - 1;
			endY = r.y - 1;
			break;
		case E:
			startX = r.x + r.width;
			startY = r.y;
			endX = r.x + r.width;
			endY = r.y + r.height - 1;
			break;
		case S:
			startX = r.x;
			startY = r.y + r.height;
			endX = r.x + r.width - 1;
			endY = r.y + r.height;
			break;
		case W:
			startX = r.x - 1;
			startY = r.y;
			endX = r.x - 1;
			endY = r.y + r.height - 1;
			break;
		default:
			throw new Error("Incorrect side " + side);
	}
	settlement.line(startX, startY, endX, endY, EntityPlacer.OBJECT_VOID);
}

public abstract boolean fitsToPlace(BuildingPlace place);

public abstract void draw();

public enum BasisBuildingSetup {
	/**
	 * Describes which methods should buildBasis use to done edges of graph
	 */
	NOT_BUILD_EDGES, CONVERT_TO_DIRECTED_TREE, KEYPOINTS_BASED
}
}
