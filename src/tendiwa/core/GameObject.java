package tendiwa.core;

public class GameObject {
	private final ObjectType type;

public GameObject(ObjectType type) {
	this.type = type;
}

public ObjectType getType() {
	return type;
}

@Override
public String toString() {
	return "GameObject{" +
		"type=" + type +
		'}';
}
}
