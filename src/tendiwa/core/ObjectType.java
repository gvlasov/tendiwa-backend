package tendiwa.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * Describes a certain type of inanimate objects that are large enough to be treated as {@link Item}s: trees, furniture,
 * wall segments
 */
public class ObjectType implements PlaceableInCell, GsonForStaticDataSerializable {
public static final ObjectType VOID = new ObjectType("void", 0, false, 0);
public static final int CLASS_DEFAULT = 0;
public static final int CLASS_WALL = 1;
public static final int CLASS_DOOR = 2;
public static final int CLASS_INTERLEVEL = 3;
private final UniqueObject uniqueness = new UniqueObject();
private final String name;
private final int passability;
private final boolean isUsable;
private final int cls;

public ObjectType(String name, int passability, boolean isUsable, int cls) {
	super();
	this.name = name;
	this.passability = passability;
	this.isUsable = isUsable;
	this.cls = cls;
}

public int getObjectClass() {
	return cls;
}

public int getPassability() {
	return passability;
}

public boolean isUsable() {
	return isUsable;
}

public String getName() {
	return name;
}

public int getId() {
	return uniqueness.id;
}

@Override
public JsonElement serialize(JsonSerializationContext context) {
	JsonArray jArray = new JsonArray();
	jArray.add(new JsonPrimitive(name));
	jArray.add(new JsonPrimitive(passability));
	return jArray;
}

@Override
public void place(Cell cell) {
	cell.object = uniqueness.id;
}

@Override
public boolean containedIn(Cell cell) {
	return cell.object == uniqueness.id;
}
}
