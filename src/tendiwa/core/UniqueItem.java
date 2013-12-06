package tendiwa.core;

/**
 * A single non-stackable item. Its main property is its id inherited from {@link UniqueObject}. Unlike {@link
 * ItemPile}s, UniqueObjects are unique -
 */
public class UniqueItem extends Item {


public UniqueItem(UniqueItemType type) {
	super(type);
}

@Override
public UniqueItemType getType() {
	return (UniqueItemType) type;
}
}
