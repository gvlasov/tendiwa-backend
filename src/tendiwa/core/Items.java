package tendiwa.core;

/**
 * Utility class containing many useful actions for {@link ItemType} handling. Some methods that initially were in
 * ItemType class were moved here to allow ItemType to be an interface rather than an abstract class.
 */

public class Items {

public static Item createItem(ItemType type) {
	if (type.isStackable()) {
		return createItemPile(type, 1);
	} else {
		return new UniqueItem(type);
	}
}

public static ItemPile createItemPile(ItemType type, int amount) {
	return new ItemPile(type, amount);
}

public static boolean isWearable(ItemType type) {
	return type instanceof Wearable;
}

public static boolean isWieldable(ItemType type) {
	return type instanceof Wieldable;
}

public static boolean isShootable(ItemType type) {
	return type instanceof Shootable;
}

public static boolean isRangedWeapon(ItemType type) {
	return type instanceof RangedWeapon;
}
public static boolean isStackable(ItemType type) {
	return type.isStackable();
}

/**
 * <p>Converts ItemType to {@link Wearable}. Actual types of ItemTypes are usually not known to client at compile time,
 * and this method helps with identifying an item type. Example:</p>
 * <pre>
 * {@code
 *
 *  if (Items.isWearable(type) != null) {
 *      wear(Items.asWearable(type));
 *  } else if (Items.isWieldable(type)) {
 *      wield(Items.asWieldable(type));
 *  } else {
 *      ...
 *  }
 * }
 * </pre>
 *
 * @param type
 * 	An type that should be Wearable.
 * @return null if {@code !(type instanceof Wearable)}, or the Wearable if it is Wearable.
 * @throws NullPointerException
 * 	If {@code type == null}
 */
public Wearable asWearable(ItemType type) {
	if (type == null) {
		throw new NullPointerException("Argument can't be null");
	}
	if (isWearable(type)) {
		return (Wearable) type;
	}
	return null;
}

/**
 * <p>Converts ItemType to {@link Wieldable}. Actual types of ItemTypes are usually not known to client at compile time,
 * and this method helps with identifying an item type. Example:</p>
 * <pre>
 * {@code
 *
 *  if (Items.isWearable(type) != null) {
 *      wear(Items.asWearable(type));
 *  } else if (Items.isWieldable(type)) {
 *      wield(Items.asWieldable(type));
 *  } else {
 *      ...
 *  }
 * }
 * </pre>
 *
 * @param type
 * 	An type that should be Wieldable.
 * @return null if {@code !(type instanceof Wieldable)}, or the Wieldable if it is Wieldable.
 * @throws NullPointerException
 * 	If {@code type == null}
 */
public Wieldable asWieldable(ItemType type) {
	if (type == null) {
		throw new NullPointerException("Argument can't be null");
	}
	if (isWieldable(type)) {
		return (Wieldable) type;
	}
	return null;
}

/**
 * <p>Converts ItemType to {@link RangedWeapon}. Actual types of ItemTypes are usually not known to client at compile
 * time, and this method helps with identifying an item type. Example:</p>
 * <pre>
 * {@code
 *
 *  if (Items.isWearable(type) != null) {
 *      wear(Items.asWearable(type));
 *  } else if (Items.isWieldable(type)) {
 *      wield(Items.asWieldable(type));
 *  } else {
 *      ...
 *  }
 * }
 * </pre>
 *
 * @param type
 * 	An type that should be RangedWeapon.
 * @return null if {@code !(type instanceof RangedWeapon)}, or the Wieldable if it is RangedWeapon.
 * @throws NullPointerException
 * 	If {@code type == null}
 */
public RangedWeapon asRangedWeapon(ItemType type) {
	if (type == null) {
		throw new NullPointerException("Argument can't be null");
	}
	if (isRangedWeapon(type)) {
		return (RangedWeapon) type;
	}
	return null;
}

/**
 * <p>Converts ItemType to {@link Shootable}. Actual types of ItemTypes are usually not known to client at compile time,
 * and this method helps with identifying an item type. Example:</p>
 * <pre>
 * {@code
 *
 *  if (Items.isWearable(type) != null) {
 *      wear(Items.asWearable(type));
 *  } else if (Items.isWieldable(type)) {
 *      wield(Items.asWieldable(type));
 *  } else {
 *      ...
 *  }
 * }
 * </pre>
 *
 * @param type
 * 	An type that should be Shootable.
 * @return null if {@code !(type instanceof Shootable)}, or the Wieldable if it is Shootable.
 * @throws NullPointerException
 * 	If {@code type == null}
 */
public static Shootable asShootable(ItemType type) {
	if (type == null) {
		throw new NullPointerException("Argument can't be null");
	}
	if (isShootable(type)) {
		return (Shootable) type;
	}
	return null;
}
public static ItemPile asStackable(Item item) {
	if (item == null) {
		throw new NullPointerException("Argument can't be null");
	}
	if (isStackable(item)) {
		return (ItemPile) item;
	}
	return null;
}

public static boolean isStackable(Item item) {
	return item.getType().isStackable();
}

PlaceableInCell asPlaceableInCell(final ItemType type) {
	if (type.isStackable()) {
		return new PlaceableInCell() {
			@Override
			public void place(HorizontalPlane terrain, int x, int y) {
				terrain.addItem(createItemPile(type, 1), x, y);
			}

			@Override
			public boolean containedIn(HorizontalPlane plane, int x, int y) {
				return false;
			}
		};
	} else {
		return new PlaceableInCell() {
			@Override
			public void place(HorizontalPlane terrain, int x, int y) {
				terrain.addItem((UniqueItem) createItem(type), x, y);
			}

			@Override
			public boolean containedIn(HorizontalPlane plane, int x, int y) {
				return false;
			}
		};
	}
}
}
