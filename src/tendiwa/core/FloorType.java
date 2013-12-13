package tendiwa.core;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.util.HashMap;
import java.util.Map;

public class FloorType implements PlaceableInCell {
protected static short lastId = 0;
private static Map<Short, FloorType> byId = new HashMap<>();
private final String name;
private static short emptinessId;
private short id;
private final boolean isLiquid;

public FloorType(String name, boolean isLiquid) {
	this.name = name;
	this.id = lastId++;
	this.isLiquid = isLiquid;
	byId.put(id, this);
	if (name.equals("emptiness")) {
		emptinessId = this.id;
	}
}

public static FloorType getById(int floorId) {
	FloorType terrainType = byId.get((short) floorId);
	if (terrainType == null) {
		throw new NullPointerException("No floor with id "+floorId);
	}
	return terrainType;
}

public static FloorType getById(short floorId) {
	return byId.get(floorId);
}

/**
 * Returns a map of object type ids to object types.
 *
 * @return Immutable map of ids to ObjectTypes.
 */
public static Map<Short, FloorType> getAll() {
	return ImmutableMap.copyOf(byId);
}


public String getName() {
	return name;
}

public short getId() {
	return id;
}

@Override
public void place(HorizontalPlane plane, int x, int y) {
	plane.placeFloor(id, x, y);
}

@Override
public boolean containedIn(HorizontalPlane plane, int x, int y) {
	return plane.getChunkWithCell(x, y).getFloor(x, y) == id;
}

public static short getEmptiness() {
	return emptinessId;
}

public static int hashXYtoFloorId(int x, int y) {
	return (x+100*y)%3+1;
}

public boolean isLiquid() {
	return isLiquid;
}
}
