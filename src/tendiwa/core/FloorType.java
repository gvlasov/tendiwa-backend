package tendiwa.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.util.HashMap;
import java.util.Map;

public final class FloorType implements PlaceableInCell, GsonForStaticDataSerializable {
private static Map<Short, FloorType> byId = new HashMap<>();
private final String name;
private final short id;
private static short lastId = 0;

public FloorType(String name) {
	this.name = name;
	this.id = lastId++;
	byId.put(id, this);
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
public void place(Cell cell) {
	cell.floor(id);
}

@Override
public boolean containedIn(Cell cell) {
	return cell.floor == id;
}

public static FloorType getById(int floorId) {
	return byId.get((short)floorId);
}
}
