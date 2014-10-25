package org.tendiwa.core;

public class EntityPlacer {
	public static final TypePlaceableInCell OBJECT_VOID = new TypePlaceableInCell() {
	};

	public static <T extends TypePlaceableInCell> void place(HorizontalPlane plane, final T entityType, int x, int y) {
		assert entityType != null;
		if (entityType instanceof FloorType) {
			plane.placeFloor((FloorType) entityType, x, y);
		} else if (entityType instanceof WallType) {
			if (entityType == WallType.VOID) {
				plane.placeWall(null, x, y);
			} else {
				plane.placeWall((WallType) entityType, x, y);
			}
		} else if (entityType instanceof ItemType) {
			if (((ItemType) entityType).isStackable()) {
				plane.addItem(new ItemPile((ItemType) entityType, 1), x, y);
			} else {
				plane.addItem(new UniqueItem((ItemType) entityType), x, y);
			}
		} else if (entityType instanceof ObjectType) {
			plane.placeObject(new GameObject() {
				@Override
				public ObjectType getType() {
					return (ObjectType) entityType;
				}
			}, x, y);
		} else {
			throw new UnsupportedOperationException(entityType.getClass().toString());
		}

	}

	public static void clearObject(HorizontalPlane plane, int x, int y) {
		plane.placeObject(null, x, y);
	}

	public static boolean containedIn(HorizontalPlane plane, TypePlaceableInCell entityType, int x, int y) {
		if (entityType instanceof FloorType) {
			return plane.getChunkWithCell(x, y).getFloor(x, y) == entityType;
		} else if (entityType instanceof WallType) {
			return plane.getChunkWithCell(x, y).getWall(x, y) == entityType;
		} else {
			throw new UnsupportedOperationException();
		}
	}
}
