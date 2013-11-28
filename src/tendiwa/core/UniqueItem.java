package tendiwa.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * A single non-stackable item. Its main property is its id inherited from {@link UniqueObject}. Unlike {@link
 * ItemPile}s, UniqueObjects are unique -
 */
public class UniqueItem implements Item, GsonForStaticDataSerializable {
final int id = new UniqueObject().id;
private ItemType type;

public UniqueItem(ItemType type) {
	super();
	this.type = type;
}

public String toString() {
	return type.getName();
}

public JsonElement serialize(JsonSerializationContext context) {
	JsonArray jArray = new JsonArray();
	jArray.add(new JsonPrimitive(type.getId()));
	jArray.add(new JsonPrimitive(id));
	return jArray;
}

@Override
public ItemType getType() {
	return type;
}
}
