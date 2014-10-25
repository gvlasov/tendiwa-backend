package org.tendiwa.core;

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
		return type.componentWearable != null;
	}

	public static boolean isWieldable(ItemType type) {
		return type.componentWieldable != null;
	}

	public static boolean isShootable(ItemType type) {
		return type.componentShootable != null;
	}

	public static boolean isRangedWeapon(ItemType type) {
		return type.componentRangedWeapon != null;
	}

	public static boolean isStackable(ItemType type) {
		return type.isStackable();
	}

	/**
	 * <p>Converts ItemType to {@link Wearable}. Actual types of ItemTypes are usually not known to client at compile
	 * time,
	 * and this method helps with identifying an item ammunitionType. Example:</p>
	 * <pre>
	 * {@code
	 *
	 *  if (Items.isWearable(ammunitionType) != null) {
	 *      wear(Items.asWearable(ammunitionType));
	 *  } else if (Items.isWieldable(ammunitionType)) {
	 *      wield(Items.asWieldable(ammunitionType));
	 *  } else {
	 *      ...
	 *  }
	 * }
	 * </pre>
	 *
	 * @param type
	 * 	An ammunitionType that should be Wearable.
	 * @return null if {@code !(ammunitionType instanceof Wearable)}, or the Wearable if it is Wearable.
	 * @throws NullPointerException
	 * 	If {@code ammunitionType == null}
	 */
	public static Wearable asWearable(ItemType type) {
		if (type == null) {
			throw new NullPointerException("Argument can't be null");
		}
		return type.componentWearable;
	}

	/**
	 * <p>Converts ItemType to {@link Wieldable}. Actual types of ItemTypes are usually not known to client at compile
	 * time,
	 * and this method helps with identifying an item ammunitionType. Example:</p>
	 * <pre>
	 * {@code
	 *
	 *  if (Items.isWearable(ammunitionType) != null) {
	 *      wear(Items.asWearable(ammunitionType));
	 *  } else if (Items.isWieldable(ammunitionType)) {
	 *      wield(Items.asWieldable(ammunitionType));
	 *  } else {
	 *      ...
	 *  }
	 * }
	 * </pre>
	 *
	 * @param type
	 * 	An ammunitionType that should be Wieldable.
	 * @return null if {@code !(ammunitionType instanceof Wieldable)}, or the Wieldable if it is Wieldable.
	 * @throws NullPointerException
	 * 	If {@code ammunitionType == null}
	 */
	public static Wieldable asWieldable(ItemType type) {
		if (type == null) {
			throw new NullPointerException("Argument can't be null");
		}
		return type.componentWieldable;
	}

	/**
	 * <p>Converts ItemType to {@link RangedWeapon}. Actual types of ItemTypes are usually not known to client at
	 * compile
	 * time, and this method helps with identifying an item ammunitionType. Example:</p>
	 * <pre>
	 * {@code
	 *
	 *  if (Items.isWearable(ammunitionType) != null) {
	 *      wear(Items.asWearable(ammunitionType));
	 *  } else if (Items.isWieldable(ammunitionType)) {
	 *      wield(Items.asWieldable(ammunitionType));
	 *  } else {
	 *      ...
	 *  }
	 * }
	 * </pre>
	 *
	 * @param type
	 * 	An ammunitionType that should be RangedWeapon.
	 * @return null if {@code !(ammunitionType instanceof RangedWeapon)}, or the Wieldable if it is RangedWeapon.
	 * @throws NullPointerException
	 * 	If {@code ammunitionType == null}
	 */
	public static RangedWeapon asRangedWeapon(ItemType type) {
		if (type == null) {
			throw new NullPointerException("Argument can't be null");
		}
		return type.componentRangedWeapon;
	}

	/**
	 * <p>Converts ItemType to {@link Shootable}. Actual types of ItemTypes are usually not known to client at compile
	 * time,
	 * and this method helps with identifying an item ammunitionType. Example:</p>
	 * <pre>
	 * {@code
	 *
	 *  if (Items.isWearable(ammunitionType) != null) {
	 *      wear(Items.asWearable(ammunitionType));
	 *  } else if (Items.isWieldable(ammunitionType)) {
	 *      wield(Items.asWieldable(ammunitionType));
	 *  } else {
	 *      ...
	 *  }
	 * }
	 * </pre>
	 *
	 * @param type
	 * 	An ammunitionType that should be Shootable.
	 * @return null if {@code !(ammunitionType instanceof Shootable)}, or the Wieldable if it is Shootable.
	 * @throws NullPointerException
	 * 	If {@code ammunitionType == null}
	 */
	public static Shootable asShootable(ItemType type) {
		if (type == null) {
			throw new NullPointerException("Argument can't be null");
		}
		return type.componentShootable;
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
