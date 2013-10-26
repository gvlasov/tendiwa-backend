package tendiwa.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.util.HashMap;
import java.util.Map;

public final class FloorType implements PlaceableInCell, GsonForStaticDataSerializable {
private static Map<Integer, FloorType> byId = new HashMap<>();
private final String name;
private final UniqueObject uniqueness;

public FloorType(String name) {
	uniqueness = new UniqueObject();
	byId.put(uniqueness.id, this);
	this.name = name;
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

public int getId() {
	return uniqueness.id;
}

@Override
public void place(Cell cell) {
	cell.floor(uniqueness.id);
}

@Override
public boolean containedIn(Cell cell) {
	return cell.floor == uniqueness.id;
}

public static FloorType getById(int floorId) {
	return byId.get(floorId);
}
}
