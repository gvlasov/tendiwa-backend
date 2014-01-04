package tendiwa.core;

public class WallType implements GameObject, TypePlaceableInCell, Resourceable {
private static final ObjectType wallObjectType = new ObjectType() {
	@Override
	public Passability getPassability() {
		return Passability.NO;
	}

	@Override
	public String getResourceName() {
		return "wall_type";
	}
};
static WallType VOID = new WallType() {
	@Override
	public String getResourceName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ObjectType getType() {
		throw new UnsupportedOperationException();
	}
};
public String name;

public void name(String name) {
	assert name != null;
	this.name = name;
}

@Override
public ObjectType getType() {
	return wallObjectType;
}

@Override
public String getResourceName() {
	return name;
}
}
