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

public void add(Item item) {
	if (item.getType().isStackable()) {
		add((ItemPile) item);
	} else {
		add((UniqueItem) item);
	}
}

public void add(ItemPile item) {
	ItemType type = item.getType();
	if (items.containsKey(type)) {
		// If there is already that type of items i a collection, change amount of the existing pile..
		((ItemPile) items.get(type)).changeAmount(item.getAmount());
	} else {
		items.put(type, item);
	}
}

public void add(UniqueItem item) {
	items.put(item.getType(), item);
}

public void removePile(ItemPile pile) {
	ItemPile pileInMap = (ItemPile) items.get(pile.getType());
	if (pile == pileInMap || pileInMap.getAmount() == pile.getAmount()) {
		items.removeAll(pile.getType());
	} else if (pile.getAmount() < pileInMap.getAmount()) {
		pileInMap.setAmount(pileInMap.getAmount() - pile.getAmount());
	} else {
		throw new Error("Incorrect pile removing: type "
			+ pile.getType().getId() + ", removing " + pile.getAmount()
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
}
