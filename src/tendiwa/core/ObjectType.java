package tendiwa.core;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes a certain type of inanimate objects that are large enough to be treated as {@link Item}s: trees, furniture,
 * wall segments
 */
public class ObjectType implements PlaceableInCell {
private final static Map<Integer, ObjectType> byId = new HashMap<>();
public static final ObjectType VOID = new ObjectType();
public static final int CLASS_DEFAULT = 0;
public static final int CLASS_WALL = 1;
public static final int CLASS_DOOR = 2;
public static final int CLASS_INTERLEVEL = 3;
private static short nextIdToAssign = 0;
private final String name;
private final Chunk.Passability passability;
private final boolean isUsable;
private final ObjectClass cls;
private final short id;

public ObjectType(String name, Chunk.Passability passability, boolean isUsable, int cls) {
	this.name = name;
	this.passability = passability;
	this.isUsable = isUsable;
	this.cls = ObjectClass.getById(cls);
	this.id = nextIdToAssign++;
	ObjectType.byId.put((int) id, this);
}

/**
 * Constructor for void object type.
 */
private ObjectType() {
	this("void", Chunk.Passability.FREE, false, 0);
	assert nextIdToAssign == 1;
}

public static ObjectType getById(int id) {
	return byId.get(id);
}

public ObjectClass getObjectClass() {
	return cls;
}

public Chunk.Passability getPassability() {
	return passability;
}

public boolean isUsable() {
	return isUsable;
}

public String getName() {
	return name;
}

public short getId() {
	return id;
}


@Override
public void place(HorizontalPlane plane, int x, int y) {
	plane.setFloor(this.id, x, y);
}

@Override
public boolean containedIn(HorizontalPlane plane, int x, int y) {
	return plane.getFloor(x, y) == this.id;
}

@Override
public String toString() {
	return name;
}

public enum ObjectClass {
	DEFAULT(0), WALL(1), DOOR(2), INTERLEVEL(3);
	private final short value;

	private ObjectClass(int value) {
		this.value = (short) value;
	}

	public static ObjectClass getById(int cls) {
		switch (cls) {
			case 0:
				return DEFAULT;
			case 1:
				return WALL;
			case 2:
				return DOOR;
			case 3:
				return INTERLEVEL;
			default:
				throw new IllegalArgumentException();
		}
	}
}

/**
 * Returns a map of object type ids to object types.
 * @return Immutable map of ids to ObjectTypes.
 */
public static Map<Integer, ObjectType> getAll() {
	return ImmutableMap.copyOf(byId);
}
}
