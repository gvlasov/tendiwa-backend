package tendiwa.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * A single non-stackable item. Its main property is its id inherited from {@link UniqueObject}. Unlike {@link
 * ItemPile}s, UniqueObjects are unique -
 */
public class UniqueItem implements Item {
final int id = new UniqueObject().id;
private ItemType type;

public UniqueItem(ItemType type) {
	super();
	this.type = type;
}

public String toString() {
	return type.getName();
}

@Override
public ItemType getType() {
	return type;
}

}
