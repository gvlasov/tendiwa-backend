package tendiwa.core;

import com.google.common.collect.ImmutableSet;
import tendiwa.core.meta.Condition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Equipment implements Iterable<Item> {
public static final UniqueItem nullItem = new UniqueItem(null) {
};
/**
 * Items equipped on a character.
 */
final Map<ApparelSlot, UniqueItem> occupiedApparelSlots = new HashMap<>();
final Item[] wieldedItems;

Equipment(int maxWieldedItems, ApparelSlot... apparel) {
	wieldedItems = new UniqueItem[maxWieldedItems];
	for (ApparelSlot slot : apparel) {
		occupiedApparelSlots.put(slot, nullItem);
	}
}

@Override
public Iterator<Item> iterator() {
	ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
	for (Item item : occupiedApparelSlots.values()) {
		if (item != nullItem) {
			builder.add(item);
		}
	}
	for (int i = 0; i < wieldedItems.length; i++) {
		if (wieldedItems[i] != null) {
			builder.add(wieldedItems[i]);
		}
	}
	return builder.build().iterator();
}

public void putOn(UniqueItem item) {
	if (!canPutOn(item)) {
		throw new UnsupportedOperationException("Can't put on " + item);
	}
	for (ApparelSlot slot : (Items.asWearable(item.getType())).getSlots()) {
		occupySlot(slot, item);
	}
}

public void wield(int handId, Item item) {
	Handedness handedness = (Items.asWieldable(item.getType())).getHandedness();
	if (handedness == Handedness.TWO_HANDS) {
		throw new UnsupportedOperationException("You can't wield items that need more than 1 hand with this method.");
	}
	if (!canWield(item)) {
		throw new UnsupportedOperationException("Can't wield item " + item + " in hand " + handId);
	}
	if (wieldedItems[handId] != null) {
		throw new RuntimeException("Can't wield item " + item + " in hand " + handId + " because that hand is already occupied by item " + wieldedItems[handId]);
	}
	wieldedItems[handId] = item;
}

public void wield(Item item) {
	if (!canWield(item)) {
		throw new UnsupportedOperationException("Can't wield item " + item);
	}
	Handedness handedness = (Items.asWieldable(item.getType())).getHandedness();
	switch (handedness) {
		case MAIN_HAND:
			if (!tryWielding(Hand.RIGHT, item)) {
				if (!tryWielding(Hand.LEFT, item)) {
					throw new RuntimeException("No available hands to wield item " + item);
				}
			}
			break;
		case OFF_HAND:
			if (!tryWielding(Hand.LEFT, item)) {
				if (!tryWielding(Hand.RIGHT, item)) {
					throw new RuntimeException("No available hands to wield item " + item);
				}
			}
			break;
		case TWO_HANDS:
			for (int i = 0; i < wieldedItems.length; i += 2) {
				if (wieldedItems[i] == null && wieldedItems[i + 1] == null) {
					wieldedItems[i] = item;
					wieldedItems[i + 1] = item;
				}
			}
			break;
	}
}

private boolean tryWielding(Hand hand, Item item) {
	for (int i = (hand == Hand.RIGHT ? 0 : 1); i < wieldedItems.length; i += 2) {
		if (wieldedItems[i] == null) {
			wieldedItems[i] = item;
			return true;
		}
	}
	return false;
}

/**
 * Checks if there is a hand free to wield a one-handed item,  or two hands to wield a two-handed item.
 *
 * @param item
 * 	An item to wield.
 * @return True if an item can be wielded by equipment bearer, false if it can't.
 */
public boolean canWield(Item item) {
	if (Items.isWieldable(item.getType())) {
		Handedness handedness = (Items.asWieldable(item.getType())).getHandedness();
		int handsToWield;
		int freeHands = 0;
		switch (handedness) {
			case MAIN_HAND:
			case OFF_HAND:
				handsToWield = 1;
				break;
			case TWO_HANDS:
				handsToWield = 2;
				break;
			default:
				throw new RuntimeException();
		}
		for (int i = 0; i < wieldedItems.length; i++) {
			if (wieldedItems[i] == null) {
				freeHands++;
				if (freeHands == handsToWield) {
					return true;
				}
			}
		}
	}
	return false;
}

public void cease(Item item) {
	boolean itemUnwielded = false;
	for (int i = 0; i < wieldedItems.length; i++) {
		if (wieldedItems[i] == item) {
			itemUnwielded = true;
			wieldedItems[i] = null;
		}
	}
	if (!itemUnwielded) {
		throw new RuntimeException("Attempting to unwield an item that is not wielded");
	}
}

public void takeOff(UniqueItem item) {
	if (!occupiedApparelSlots.values().contains(item)) {
		throw new UnsupportedOperationException("Attempting to unequip an item " + item + " that is not equipped");
	}
	for (ApparelSlot slot : (Items.asWearable( item.getType())).getSlots()) {
		occupiedApparelSlots.put(slot, nullItem);
	}
}

public boolean canPutOn(UniqueItem item) {
	if (!Items.isWearable(item.getType())) {
		return false;
	}
	for (ApparelSlot slot : (Items.asWearable(item.getType())).getSlots()) {
		if (!occupiedApparelSlots.containsKey(slot)) {
			throw new UnsupportedOperationException("Character doesn't have slot " + slot + " to equip item " + item);
		}
		if (occupiedApparelSlots.get(slot) != nullItem) {
			return false;
		}
	}
	return true;
}

private void occupySlot(ApparelSlot slot, UniqueItem item) {
	if (!hasSlot(slot)) {
		throw new UnsupportedOperationException("Equipment doesn't have slot " + slot);
	}
	if (occupiedApparelSlots.get(slot) != nullItem) {
		throw new UnsupportedOperationException("Slot " + slot + " is already occupied");
	}
	occupiedApparelSlots.put(slot, item);
}

boolean hasSlot(ApparelSlot slot) {
	return occupiedApparelSlots.containsKey(slot);
}

boolean isSlotOccupied(ApparelSlot slot) {
	return occupiedApparelSlots.get(slot) != nullItem;
}

public void occupySlots(AspectApparel aspectApparel) {
	for (ApparelSlot slot : aspectApparel.getSlots()) {

	}
}

/**
 * Returns first wielded item that passes some condition.
 *
 * @param condition
 * 	Condition to pass.
 * @return An item that is currently wielded and passes the condition, or null if there's no such item.
 */
public Item getWieldedWeaponThatIs(Condition<Item> condition) {
	for (Item weapon : wieldedItems) {
		if (weapon == nullItem || weapon == null) {
			continue;
		}
		if (condition.check(weapon)) {
			return weapon;
		}
	}
	return null;
}

public boolean isWielded(Item weapon) {
	for (int i = 0; i < wieldedItems.length; i++) {
		if (wieldedItems[i] == weapon) {
			return true;
		}
	}
	return false;
}

private enum Hand {
	LEFT, RIGHT
}
}
