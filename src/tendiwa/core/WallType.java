package tendiwa.core;

public interface WallType extends GameObject, TypePlaceableInCell, Resourceable {
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

}
