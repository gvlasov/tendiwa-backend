package tendiwa.core;

import java.util.HashMap;
import java.util.Map;

public class WallType implements PlaceableInCell {
public static final WallType NO_WALL;
public static final short NO_WALL_ID;
protected static short lastId = 0;
private static Map<Short, WallType> byId = new HashMap<>();

static {
	NO_WALL = new WallType("no_wall");
	NO_WALL_ID = NO_WALL.getId();
}

private final String name;
private short id;

public WallType(String name) {
	this.name = name;
	this.id = lastId++;
	byId.put(id, this);
}

public static int getNumberOfWallTypes() {
	return lastId-1;
}

public static WallType getById(short wallId) {
	return byId.get(wallId);
}

public static WallType getById(int wallId) {
	return byId.get((short) wallId);
}

@Override
public void place(HorizontalPlane terrain, int x, int y) {
	terrain.placeWall(this.id, x, y);
}

@Override
public boolean containedIn(HorizontalPlane plane, int x, int y) {
	return plane.getChunkWithCell(x, y).getWall(x, y) == id;
}

public String getName() {
	return name;
}

public short getId() {
	return id;
}
}
