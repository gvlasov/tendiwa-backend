package tendiwa.core;

/**
 * Represents an item or a pile of items occupying a single slot in character's inventory.
 */
public class Item {
final ItemType type;

public Item(ItemType type) {
	this.type = type;
}

public ItemType getType() {
	return type;
}


}
