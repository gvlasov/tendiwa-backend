
package tendiwa.core;

import tendiwa.core.meta.Coordinate;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class TerrainBasics {
public final int x;
public final int y;
public HashMap<Integer, Character> characters = new HashMap<>();
public ArrayList<Ceiling> ceilings;
protected HashMap<Integer, ItemCollection> items = new HashMap<>();
Cell[][] cells;

public TerrainBasics(int x, int y) {
	this.x = x;
	this.y = y;
}

public static Coordinate[] vector(int startX, int startY, int endX, int endY) {
	int l = Math.round(Math.max(Math.abs(endX - startX),
		Math.abs(endY - startY)));
	float x[] = new float[l + 2];
	float y[] = new float[l + 2];
	Coordinate result[] = new Coordinate[l + 1];

	x[0] = startX;
	y[0] = startY;

	if (startX == endX && startY == endY) {
		result = new Coordinate[1];
		result[0] = new Coordinate(startX, startY);
		return result;
	}
	float dx = (endX - startX) / (float) l;
	float dy = (endY - startY) / (float) l;
	for (int i = 1; i <= l; i++) {
		x[i] = x[i - 1] + dx;
		y[i] = y[i - 1] + dy;
	}
	x[l + 1] = endX;
	y[l + 1] = endY;

	for (int i = 0; i <= l; i++) {
		result[i] = new Coordinate(Math.round(x[i]), Math.round(y[i]));
	}
	return result;
}


public void addItem(UniqueItem item, int x, int y) {
	items.get(x * getWidth() + y).add(item);
}

public void addItem(ItemPile item, int x, int y) {
	items.get(x * getWidth() + y).add(item);
}

public boolean hasObject(int x, int y) {
	return cells[x][y].object() != StaticData.VOID;
}

public boolean hasCharacter(int x, int y) {
	for (Character character : characters.values()) {
		if (character.x == x && character.y == y) {
			return true;
		}
	}
	return false;
}

public void createCeiling(Rectangle ceiling, int type) {
	ceilings.add(new Ceiling(ceiling, type));
}

public void setElement(int x, int y, PlaceableInCell placeable) {
	placeable.place(cells[x][y]);
}

public ItemCollection getItems(int x, int y) {
	return items.get(x * getWidth() + y);
}

public float distance(int startX, int startY, int endX, int endY) {
	return (float) Math.sqrt(Math.pow(startX - endX, 2)
		+ Math.pow(startY - endY, 2));
}

public void removeItem(UniqueItem item, int x, int y) {
	items.get(x * getWidth() + y).removeUnique(item);
}

public void removeItem(ItemPile item, int x, int y) {
	items.get(x * getWidth() + y).removePile(item);
}

public void removeObject(int x, int y) {
	ObjectType.VOID.place(cells[x][y]);
}

public void computePassability(int x, int y) {
	Cell cell = cells[x][y];
	if (cell.floor() == StaticData.getFloorType("water").getId()) {
		cell.setPassability(Passability.SEE);
	} else if (cell.object() != StaticData.VOID) {
		cell.setPassability(ObjectType.getById(cell.object()).getPassability());
	} else if (cell.character() != null) {
		cell.setPassability(Passability.SEE);
	} else {
		cell.setPassability(Passability.SEE);
	}
}

public boolean isDoor(int x, int y) {
	return ObjectType.getById(cells[x][y].object()).getObjectClass() == ObjectType.ObjectClass.DOOR;
}

public abstract int getWidth();

public abstract int getHeight();

public Cell[][] copyCells() {
	return cells.clone();
}

public enum Passability {
	FREE((byte) 0), SEE((byte) 1), NO((byte) 2);
	final byte value;

	Passability(byte value) {
		this.value = value;
	}

	public byte value() {
		return value;
	}
	public static Passability value2Passability(byte value) {
		switch (value) {
			case 0:
				return FREE;
			case 1:
				return SEE;
			case 2:
				return NO;
			default:
				assert false;
				throw new IllegalArgumentException("Illegal passability byte representation "+value);
		}
	}
}

}
