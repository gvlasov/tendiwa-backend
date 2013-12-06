package tendiwa.core;

public abstract class StackableItemType extends ItemType implements PlaceableInCell {
protected StackableItemType() {
}

@Override
public void place(HorizontalPlane terrain, int x, int y) {
	terrain.addItem(new ItemPile(this, 1), x, y);
}

@Override
public boolean containedIn(HorizontalPlane plane, int x, int y) {
	return false;
}

@Override
public ItemPile createItem() {
	return createItem(1);
}

public ItemPile createItem(int amount) {
	return new ItemPile(this, amount);
}
}
