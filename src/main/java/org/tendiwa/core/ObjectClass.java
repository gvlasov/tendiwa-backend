package org.tendiwa.core;

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
