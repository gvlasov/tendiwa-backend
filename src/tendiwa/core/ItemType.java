package tendiwa.core;

public abstract class ItemType {

protected ItemType() {
}

public abstract Material getMaterial();

public abstract String getResourceName();

public abstract double getWeight();

public abstract double getVolume();

public abstract boolean isStackable();

public Item createItem() {
	if (isStackable()) {
		return ((StackableItemType) this).createItem();
	} else {
		return ((UniqueItemType) this).createItem();
	}
}

public boolean isWearable() {
	try {
		Wearable test = (Wearable) this;
	} catch (ClassCastException e) {
		return false;
	}
	return true;
}

public boolean isWieldable() {
	try {
		Wieldable test = (Wieldable) this;
	} catch (ClassCastException e) {
		return false;
	}
	return true;
}

public boolean isRangedWeapon() {
	try {
		RangedWeapon test = (RangedWeapon) this;
	} catch (ClassCastException e) {
		return false;
	}
	return true;
}

public boolean isShootable() {
	try {
		Shootable test = (Shootable) this;
	} catch (ClassCastException e) {
		return false;
	}
	return true;
}
}
