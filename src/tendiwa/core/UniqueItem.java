package tendiwa.core;

/**
 * A single non-stackable item. Its main property is its id inherited from {@link UniqueObject}. Unlike {@link
 * ItemPile}s, UniqueObjects are unique -
 */
public class UniqueItem extends Item {


public UniqueItem(ItemType type) {
	super(type);
}

@Override
public UniqueItem takeSingleItem() {
	return this;
}

@Override
public boolean isContainedIn(ItemCollection items) {
	return items.items.containsValue(this);
}

}
