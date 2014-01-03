package tendiwa.core;

public class GameObjects {
public static Usable asUsable(ObjectType type) {
	if (type == null) {
		throw new NullPointerException("Argument can't be null");
	}
	if (isUsable(type)) {

		return (Usable) type;
	} else {
		return null;
	}
}

private static boolean isUsable(ObjectType type) {
	return type instanceof Usable;
}
}
