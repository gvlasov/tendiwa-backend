package tendiwa.core;

public interface WallType extends TypePlaceableInCell, Resourceable {
static WallType VOID = new WallType() {
	@Override
	public String getResourceName() {
		return null;
	}
};

}
