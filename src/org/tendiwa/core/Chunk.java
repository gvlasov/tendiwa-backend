package org.tendiwa.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.tendiwa.core.meta.Coordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chunk implements Serializable {
public static final byte SIZE = 32;
public final int x;
public final int y;
public HorizontalPlane plane;
protected HashMap<Integer, ItemCollection> items = new HashMap<>();
protected Table<Integer, CardinalDirection, BorderObject> borderObjects = HashBasedTable.create();
Map<Integer, Character> characters = new HashMap<>();
FloorType[][] floors;
GameObject[][] objects;
private ArrayList<SoundSource> soundSources = new ArrayList<>();
transient private TimeStream timeStream;

public Chunk(HorizontalPlane plane, int x, int y) {
	this.x = x;
	this.y = y;
	this.floors = new FloorType[SIZE][SIZE];
	this.objects = new GameObject[SIZE][SIZE];
	this.plane = plane;
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

/**
 * Returns a hash of 2 numbers: x and y coordinates in a coordinate system where maximum value of {@code y} is {@code
 * height-1}.
 *
 * @param x
 * 	X coordinate in any coordinate system
 * @param y
 * 	Y coordinate in any coordinate system
 * @param height
 * 	Height of that coordinate system
 * @return Hash of {@code x} and {@code y} values.
 * @see Chunk#cellHashToCoords(int, int) For a reverse operation
 */
public static int cellHash(int x, int y, int height) {
	return x * height + y;
}

/**
 * Returns 2 integers: x and y coordinates of a coordinate system with given {@code height}. Those integers are
 * extracted from one integer, which is a hash of those two (no two pairs of x and y coordinates share the same hash).
 *
 * @param hash
 * 	Hash of 2 integers: x and y coordinates of any coordinate system.
 * @param height
 * 	Height of coordinate system in which those integers were hashed.
 * @return Array of 2 integers: [0] is x coordinate and [1] is y coordinate.
 */
public static int[] cellHashToCoords(int hash, int height) {
	return new int[]{hash / height, hash % height};
}

public BorderObject getBorderObject(int x, int y, CardinalDirection side) {
	assert side != null;
	if (side != Directions.N && side != Directions.W) {
		if (side == Directions.E) {
			side = Directions.W;
			x += 1;
		} else {
			assert side == Directions.S;
			side = Directions.N;
			y += 1;
		}
	}
	// At this point, side is either N or W
	int key = cellHash(x, y);
	return borderObjects.get(key, side);
}

public BorderObject setBorderObject(int x, int y, CardinalDirection side, BorderObjectType type) {
	assert side != null;
	if (side != Directions.N && side != Directions.W) {
		if (side == Directions.E) {
			side = Directions.W;
			x += 1;
		} else {
			assert side == Directions.S;
			side = Directions.N;
			y += 1;
		}
	}
	int key = cellHash(x, y);
	BorderObject value = new BorderObject(type);
	borderObjects.put(key, side, value);
	return value;
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

protected NonPlayerCharacter createCharacter(int relX, int relY, CharacterType characterType, String name, int fraction) {
	NonPlayerCharacter character = new NonPlayerCharacter(plane, characterType, x + relX, y + relY, name);
	character.setFraction(fraction);
	addCharacter(character);
		/*
		 * timeStream.fireEvent(new EventCharacterAppear( character.getId(),
		 * character.x, character.y, character.getType().getId(),
		 * character.name, character.getEffects(), character.getEquipment(),
		 * character.getFraction()));
		 */
	timeStream.notifyNeighborsVisiblilty(character);
	character.getVisibleEntities();
	return character;
}

void addCharacter(Character character) {
	int key = cellHash(character.x, character.y);
	if (characters.containsKey(key)) {
		throw new RuntimeException("Trying to place character " + character + " in cell " + character.x + ":" + character.y + " where there is already character " + characters.get(key));
	} else {
		characters.put(key, character);
	}
}

void removeCharacter(Character character) {
	Character removedCharacter = characters.remove(character.x * Chunk.SIZE + character.y);
	if (removedCharacter == null) {
		throw new RuntimeException("Character " + character + " can't be removed from chunk because it doesn't contain that character");
	}
}

public void removeObject(int x, int y) {
	objects[x - this.x][y - this.y] = null;
	if (Tendiwa.getPlayerCharacter().isCellVisible(x, y) && Tendiwa.getPlayerCharacter().isVisionCacheEmpty()) {
		Tendiwa.getPlayerCharacter().invalidateVisionCache();
	}
	throw new UnsupportedOperationException();
}

public void createSoundSource(int x, int y, SoundType type) {
	soundSources.add(new SoundSource(x, y, type, 1000));
	throw new UnsupportedOperationException();
}

public void removeSoundSource(int x, int y) {
	int size = soundSources.size();
	for (int i = 0; i < size; i++) {
		Sound s = soundSources.get(i);
		if (s.x == x && s.y == y) {
			soundSources.remove(i);
			throw new UnsupportedOperationException();
//			return;
		}
	}
	throw new Error("Sound source at " + x + ":" + y + " not found");
}

/**
 * Sets or unsets a TimeStream this Chunk belongs to.
 *
 * @param timeStream
 * 	A TimeStream, or null to let this Chunk belong to no TimeStream.
 */
public void setTimeStream(TimeStream timeStream) {
	if (timeStream != null && this.timeStream != null) {
		throw new RuntimeException(this + " is already in a time stream!");
	}
	this.timeStream = timeStream;
}

public String toString() {
	return "Chunk-" + x + ":" + y;
}

/**
 * Checks if this chunk is inside a TimeStream
 *
 * @param timeStream
 * @return True if it is, false otherwise (if it belongs to another TimeStream or to no TimeStream at all)
 */
public boolean belongsToTimeStream(TimeStream timeStream) {
	return this.timeStream == timeStream;
}

public void setFloor(FloorType floor, int x, int y) {
	floors[x - this.x][y - this.y] = floor;
}

public Character getCharacter(int x, int y) {
	return characters.get(cellHash(x, y));
}

public GameObject getGameObject(int x, int y) {
	return objects[x - this.x][y - this.y];
}

/**
 * Places an Item on a certain cell in this Chunk.
 *
 * @param x
 * 	X coordinate of a cell in chunk coordinates.
 * @param y
 * 	Y coordinate of a cell in chunk coordinates.
 */
public void addItem(Item item, int x, int y) {
	int key = cellHash(x, y);
	ItemCollection itemsInCell = items.get(key);
	if (itemsInCell == null) {
		itemsInCell = new ItemCollection();
		items.put(key, itemsInCell);
	}
	itemsInCell.add(item);
}

public boolean hasObject(int x, int y) {
	return objects[x - this.x][y - this.y] != null;
}

public boolean hasCharacter(int x, int y) {
	for (Character character : characters.values()) {
		if (character.getX() == x && character.getY() == y) {
			return true;
		}
	}
	return false;
}

public ItemCollection getItems(int x, int y) {
	return items.get(cellHash(x, y));
}

public float distance(int startX, int startY, int endX, int endY) {
	return (float) Math.sqrt(Math.pow(startX - endX, 2)
		+ Math.pow(startY - endY, 2));
}

public void removeItem(UniqueItem item, int x, int y) {
	items.get(cellHash(x, y)).removeUnique(item);
}

public void removeItem(ItemPile item, int x, int y) {
	items.get(cellHash(x, y)).removePile(item);
}

private GameObject getObject(int x, int y) {
	return objects[x - this.x][y - this.y];
}

/**
 * @param x
 * 	Absolute x coordinate.
 * @param y
 * 	Absolute y coordinate.
 * @return Id of {@link FloorType} in that cell.
 */
public FloorType getFloor(int x, int y) {
	return floors[x - this.x][y - this.y];
}

/**
 * Returns id of a wall in the specified cell.
 *
 * @param x
 * 	X coordinate of cell in chunk coordinates.
 * @param y
 * 	Y coordinate of cell in chunk coordinates.
 * @return Id of a wall in the specified cell.
 */
public GameObject getWall(int x, int y) {
	return objects[x - this.x][y - this.y];
}

public void setWall(WallType wall, int x, int y) {
	objects[x - this.x][y - this.y] = wall;
}

/**
 * Checks if there are any items, either {@link ItemPile}s of {@link UniqueItem}s, in this cell.
 *
 * @param x
 * 	X coordinate of cell in chunk coordinates.
 * @param y
 * 	Y coordinate of cell in chunk coordinates.
 * @return Id of a wall in the specified cell.
 */
public boolean hasAnyItems(int x, int y) {
	int key = cellHash(x, y);
	return items.containsKey(key) && items.get(key).size() > 0;
}

public void setObject(GameObject object, int x, int y) {
	objects[x - this.x][y - this.y] = object;
}

private int cellHash(int x, int y) {
	return x * SIZE + y;
}

public boolean hasBorderObject(int x, int y, CardinalDirection side) {
	assert side == Directions.N || side == Directions.W;
	return borderObjects.contains(cellHash(x, y), side);
}
}
