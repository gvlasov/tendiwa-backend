package tendiwa.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import tendiwa.core.meta.Condition;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ItemCollection stores a group of items, representing, for example, character's inventory or a heap of items lying on
 * the ground.
 */
public class ItemCollection implements Iterable<Item> {
public ItemCollection() {

}

protected final Multimap<ItemType, Item> items = HashMultimap.create();

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
		// If there is already that ammunitionType of items in a collection, change amount of the existing pile..
		ItemPile itemPile = (ItemPile) items.get(type).iterator().next();
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
	Collection<Item> itemsOfThatType = items.get(pile.getType());
	if (itemsOfThatType.isEmpty()) {
		throw new RuntimeException("Can't remove an item of type " + pile.getType() + " because there are no items of that type in this ItemCollection");
	}
	ItemPile pileInMap = (ItemPile) itemsOfThatType.iterator().next();
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
	return item.isContainedIn(this);
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

/**
 * <p>Remove one item from this ItemCollection.</p><p>If {@code Item}'s {@link ItemType} is {@link
 * tendiwa.core.ItemType#isStackable()}, then one piece will be extracted from that pile, removing that pile in case
 * that was the last piece.</p><p>If {@code item}'s {@link ItemType} is not {@link tendiwa.core.ItemType#isStackable()},
 * then it will be simply removed from this ItemCollection.</p>
 *
 * @param item
 * 	An item from this ItemCollection.
 * @throws NoSuchElementException
 * 	If {@code item} is not in this ItemCollection.
 */
public Item removeOne(Item item) {
	if (!contains(item)) {
		throw new NoSuchElementException(item + " is not in this ItemCollection");
	}
	if (item.getType().isStackable()) {
		ItemPile pile = (ItemPile) item;
		if (pile.getAmount() == 1) {
			removePile(pile);
			return pile;
		} else {
			pile.changeAmount(-1);
			return new ItemPile(pile.getType(), 1);
		}
	} else {
		removeUnique((UniqueItem) item);
		return item;
	}
}

/**
 * Returns an item that satisfies a condition. First item that satisfies that condition will be picked.
 *
 * @param condition
 * 	A condition to be satisfied.
 * @return An item that satisfies a condition or null if no items satisfy that condition.
 */
public Item getItem(Condition<Item> condition) {
	for (Item item : this) {
		if (condition.check(item)) {
			return item;
		}
	}
	return null;
}
}
