package org.tendiwa.core;

public class ItemPile extends Item {
private int amount;

public ItemPile(ItemType type, int amount) {
	super(type);
	this.amount = amount;
}

public int changeAmount(int difference) {
	if (this.amount + difference <= 0) {
		throw new Error("Item's amount decreased by more than this item contained (" + this.amount + " - " + difference + ")");
	}
	this.amount += difference;
	return this.amount;
}

public int getAmount() {
	return amount;
}

public int setAmount(int amount) {
	this.amount = amount;
	return amount;
}

public int hashCode() {
	return super.hashCode();
}

@Override
public String toString() {
	return amount + " " + getType().getResourceName();
}

@Override
public Item takeSingleItem() {
	return new ItemPile(type, 1);
}

@Override
public boolean isContainedIn(ItemCollection items) {
	return items.items.containsKey(type) && ((ItemPile)items.items.get(type).iterator().next()).amount >= amount;
}
}
