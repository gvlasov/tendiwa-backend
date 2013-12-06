package tendiwa.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Iterator;

/**
 * ItemCollection stores a group of items, representing, for example, character's inventory or a heap of items lying on
 * the ground.
 */
public class ItemCollection implements Iterable<Item> {
protected final Multimap<ItemType, Item> items = HashMultimap.create();

public ItemCollection() {

}

public Item add(Item item) {
	if (item.getType().isStackable()) {
		return add((ItemPile) item);
	} else {
		return add((UniqueItem) item);
	}
}

public ItemPile add(ItemPile item) {
	ItemType type = item.getType();
	if (items.containsKey(type)) {
		// If there is already that type of items i a collection, change amount of the existing pile..
		ItemPile itemPile = (ItemPile) items.get(type);
		itemPile.changeAmount(item.getAmount());
		return itemPile;
	} else {
		items.put(type, item);
		return item;
	}
}

public UniqueItem add(UniqueItem item) {
	items.put(item.getType(), item);
	return item;
}

public void removePile(ItemPile pile) {
	ItemPile pileInMap = (ItemPile) items.get(pile.getType());
	if (pile == pileInMap || pileInMap.getAmount() == pile.getAmount()) {
		items.removeAll(pile.getType());
	} else if (pile.getAmount() < pileInMap.getAmount()) {
		pileInMap.setAmount(pileInMap.getAmount() - pile.getAmount());
	} else {
		throw new RuntimeException("Incorrect pile removing: type "
			+ pile.getType().getResourceName() + ", removing " + pile.getAmount()
			+ ", has " + pileInMap.getAmount());
	}
}

public void removeUnique(UniqueItem item) {
	items.remove(item.getType(), item);
}

public boolean contains(Item item) {
	return items.containsValue(item);
}

public boolean containsPile(ItemType type, int amount) {
	return items.containsKey(type)
		&& ((ItemPile) items.get(type).iterator().next()).getAmount() >= amount;
}

public int size() {
	return items.size();
}

@Override
public Iterator<Item> iterator() {
	return items.values().iterator();
}

public void removeItem(Item item) {
	if (!items.containsValue(item)) {
		throw new IllegalArgumentException("Can't remove an item that is not already in this ItemCollection");
	}
	items.remove(item.getType(), item);
}

@Override
public String toString() {
	return "ItemCollection{" +
		"items=" + items.values() +
		'}';
}
}
