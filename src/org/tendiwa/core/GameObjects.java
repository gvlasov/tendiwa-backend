package org.tendiwa.core;

public class GameObjects {
public static Usable asUsable(ObjectType type) {
	if (type == null) {
		throw new NullPointerException("Argument can't be null");
	}
	return type.usableComponent;
}

private static boolean isUsable(ObjectType type) {
	return type.usableComponent != null;
}
}
