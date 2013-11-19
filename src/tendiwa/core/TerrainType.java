package tendiwa.core;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.util.HashMap;
import java.util.Map;

public class TerrainType implements PlaceableInCell, GsonForStaticDataSerializable {
protected static short lastFloorId = 0;
protected static short lastWallId = -1;
private static Map<Short, TerrainType> byId = new HashMap<>();
private final String name;
private static short emptinessId;
private short id;
private TerrainClass terrainClass;

public TerrainType(String name, TerrainClass terrainClass) {
	this.name = name;
	this.terrainClass = terrainClass;
	if (terrainClass == TerrainClass.WALL) {
		this.id = lastWallId--;
	} else {
		assert terrainClass == TerrainClass.FLOOR;
		this.id = lastFloorId++;
	}
	byId.put(id, this);
	if (name.equals("emptiness")) {
		emptinessId = this.id;
	}
}

public static TerrainType getById(int floorId) {
	TerrainType terrainType = byId.get((short) floorId);
	if (terrainType == null) {
		throw new NullPointerException("No terrain with id "+floorId);
	}
	return terrainType;
}

public static TerrainType getById(short floorId) {
	return byId.get(floorId);
}

/**
 * Returns a map of object type ids to object types.
 *
 * @return Immutable map of ids to ObjectTypes.
 */
public static Map<Short, TerrainType> getAll() {
	return ImmutableMap.copyOf(byId);
}

public static boolean isFloor(int north) {
	return byId.get((short) north).getTerrainClass() == TerrainClass.FLOOR;
}

public static int getNumberOfWallTypes() {
	return -lastWallId-1;
}

@Override
public JsonElement serialize(JsonSerializationContext context) {
	JsonArray jArray = new JsonArray();
	jArray.add(new JsonPrimitive(name));
	return jArray;
}

public String getName() {
	return name;
}

public short getId() {
	return id;
}

@Override
public void place(HorizontalPlane plane, int x, int y) {
	plane.placeTerrainElement(id, x, y);
}

@Override
public boolean containedIn(HorizontalPlane plane, int x, int y) {
	return plane.getChunkWithCell(x, y).getTerrainElement(x, y) == id;
}

public Chunk.Passability getPassability() {
	return terrainClass == TerrainClass.FLOOR ? Chunk.Passability.FREE : Chunk.Passability.NO;
}

public TerrainClass getTerrainClass() {
	return terrainClass;
}

public static short getEmptiness() {
	return emptinessId;
}

public enum TerrainClass {
	FLOOR, WALL
}
public static int hashXYtoFloorId(int x, int y) {
	return (x+100*y)%3+1;
}
}
