package tendiwa.core;

public abstract class UniqueItemType extends ItemType implements PlaceableInCell {
protected UniqueItemType() {
}

@Override
public boolean isStackable() {
	return false;
}

@Override
public void place(HorizontalPlane terrain, int x, int y) {
	terrain.addItem(new UniqueItem(this), x, y);
}

@Override
public boolean containedIn(HorizontalPlane plane, int x, int y) {
	return false;
}

@Override
public UniqueItem createItem() {
	return new UniqueItem(this);
}
}
