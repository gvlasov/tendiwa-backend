package tendiwa.core;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class ItemType implements GsonForStaticDataSerializable, PlaceableInCell {
private static int lastId = 0;
private final String name;
private final double weight;
private final double volume;
private final Material material;
private final boolean stackable;
private final int id;
private ImmutableSet<Aspect> aspects;

public ItemType(String name, ImmutableSet<Aspect> aspects, double weight, double volume, Material material, boolean stackable) {
	this.name = name;
	this.weight = weight;
	this.volume = volume;
	this.material = material;
	this.aspects = aspects;
	this.stackable = stackable;
	id = lastId++;
}

/**
 * Checks if this ItemType implements a particular {@link Aspect}
 *
 * @param aspect
 * 	Aspect of item.
 * @return true if it does implement an Aspect, false otherwise.
 */
public boolean hasAspect(AspectName aspect) {
	for (Aspect a : aspects) {
		if (a.getName() == aspect) {
			return true;
		}
	}
	return false;
}

public Aspect getAspect(AspectName aspect) {
	for (Aspect a : aspects) {
		if (a.getName() == aspect) {
			return a;
		}
	}
	return null;
}

public boolean isWeapon() {
	return true;
}

public String getName() {
	return name;
}

public boolean isStackable() {
	return stackable;
}

public String toString() {
	return name;
}

@Override
public JsonElement serialize(JsonSerializationContext context) {
	JsonArray jArray = new JsonArray();
	JsonArray jAspectsArray = new JsonArray();
	jArray.add(new JsonPrimitive(name));
	jArray.add(new JsonPrimitive(weight));
	jArray.add(new JsonPrimitive(volume));
	jArray.add(new JsonPrimitive(material.getId()));
	jArray.add(new JsonPrimitive(stackable));
	jArray.add(new JsonPrimitive(stackable));
	for (Aspect aspect : aspects) {
		jAspectsArray.add(context.serialize(aspect));
	}
	return jArray;
}

public int getId() {
	return id;
}

@Override
public void place(HorizontalPlane terrain, int x, int y) {
	if (stackable) {
		terrain.addItem(createStackable(1), x, y);
	} else {
		terrain.addItem(createUnique(), x, y);
	}
}

private UniqueItem createUnique() {
	if (stackable) {
		throw new RuntimeException("Attempting to create a unstackable item of an stackable type");
	}
	return new UniqueItem(this);
}

private ItemPile createStackable(int amount) {
	if (!stackable) {
		throw new RuntimeException("Attempting to create a stackable item of an unstackable type");
	}
	return new ItemPile(this, amount);
}

@Override
public boolean containedIn(HorizontalPlane plane, int x, int y) {
	return false;
}
}
