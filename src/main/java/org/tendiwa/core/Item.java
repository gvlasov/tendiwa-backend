package org.tendiwa.core;

import org.tendiwa.lexeme.Localizable;

/**
 * Represents an item or a pile of items occupying a single slot in character's inventory.
 */
public abstract class Item implements Projectile, Localizable {
	final ItemType type;

	public Item(ItemType type) {
		this.type = type;
	}

	public ItemType getType() {
		return type;
	}

	public abstract Item takeSingleItem();

	@Override
	public String getResourceName() {
		return type.getResourceName();
	}

	@Override
	public String getLocalizationId() {
		return type.getLocalizationId();
	}

	public abstract boolean isContainedIn(ItemCollection items);
}
