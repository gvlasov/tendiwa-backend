package tendiwa.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import tendiwa.core.meta.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;

public class Chunk extends TerrainBasics implements GsonForStaticDataSerializable {
public static final byte SIZE = 30;
public HorizontalPlane plane;
public Chunk neighborN;
public Chunk neighborE;
public Chunk neighborS;
public Chunk neighborW;
private HashSet<Character> characters = new HashSet<>();
private HashSet<NonPlayerCharacter> nonPlayerCharacters = new HashSet<>();
private ArrayList<SoundSource> soundSources = new ArrayList<>();
private TimeStream timeStream;

public Chunk(HorizontalPlane plane, int x, int y) {
	super(x, y);
	this.plane = plane;
	this.cells = new Cell[Chunk.SIZE][Chunk.SIZE];
	for (byte i = 0; i < SIZE; i++) {
		for (byte j = 0; j < SIZE; j++) {
			cells[i][j] = new Cell();
		}
	}
}

public Cell getCell(int x, int y) {
	return cells[x - this.x][y - this.y];
}

public Chunk getNeighbor(CardinalDirection side) {
	if (side == null) {
		throw new NullPointerException();
	}
	switch (side) {
		case N:
			return neighborN;
		case E:
			return neighborE;
		case S:
			return neighborE;
		case W:
		default:
			return neighborE;
	}
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

protected NonPlayerCharacter createCharacter(int relX, int relY, int characterTypeId, String name, int fraction) {
	NonPlayerCharacter character = new NonPlayerCharacter(plane, StaticData.getCharacterType(characterTypeId), x + relX, y + relY, name);
	character.setFraction(fraction);
	characters.add(character);
	nonPlayerCharacters.add(character);
	cells[relX][relY].character(character);
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

void addCharacter(Character ch) {
	cells[ch.x - x][ch.y - y].character(ch);
	characters.add(ch);
}

void removeCharacter(Character character) {
	cells[character.x - x][character.y - y].setPassability(Passability.FREE);
	cells[character.x - x][character.y - y].character(false);
	characters.remove(character);
}

public void place(int x, int y, PlaceableInCell type) {
	type.place(cells[x][y]);
	throw new UnsupportedOperationException();
}

public void removeObject(int x, int y) {
	super.removeObject(x, y);
	for (NonPlayerCharacter ch : nonPlayerCharacters) {
		if (ch.initialCanSee(x, y)) {
			ch.getVisibleEntities();
		}
	}
	if (Tendiwa.getPlayer().canSee(x,y) && Tendiwa.getPlayer().isVisionCacheEmpty()) {
		Tendiwa.getPlayer().invalidateVisionCache();
	}
	throw new UnsupportedOperationException();
}

/**
 * Places a UniqueItem on a certain cell in this Chunk.
 *
 * @param x
 * 		Relative coordinates of cell
 * @param y
 * 		Relative coordinates of cell
 */
public void addItem(UniqueItem item, int x, int y) {
	super.addItem(item, x, y);
	throw new UnsupportedOperationException();
}

/**
 * Places an ItemPile on a certain cell in this Chunk.
 *
 * @param x
 * 		Relative coordinates of cell
 * @param y
 * 		Relative coordinates of cell
 */
public void addItem(ItemPile pile, int x, int y) {
	super.addItem(pile, x, y);
	throw new UnsupportedOperationException();
}

public void removeItem(ItemPile pile, int x, int y) {
	super.removeItem(pile, x, y);
	throw new UnsupportedOperationException();
}

public void removeItem(UniqueItem item, int x, int y) {
	super.removeItem(item, x, y);
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
 * 		A TimeStream, or null to let this Chunk belong to no TimeStream.
 */
public void setTimeStream(TimeStream timeStream) {
	if (timeStream != null && this.timeStream != null) {
		throw new RuntimeException(this + " is already in a time stream!");
	}
	this.timeStream = timeStream;
}

public int getWidth() {
	return Chunk.SIZE;
}

public int getHeight() {
	return Chunk.SIZE;
}

public String toString() {
	return "Chunk " + x + " " + y;
}

public int[] getContentsAsIntegerArray() {
	int[] contents = new int[Chunk.SIZE * Chunk.SIZE * 2];
	int u = 0;
	for (int y = 0; y < Chunk.SIZE; y++) {
		for (int x = 0; x < Chunk.SIZE; x++) {
			contents[u++] = cells[x][y].floor;
			contents[u++] = cells[x][y].object;
		}
	}
	return contents;
}

@Override
public JsonElement serialize(JsonSerializationContext context) {
	JsonArray jArray = new JsonArray();
	for (int j = 0; j < SIZE; j++) {
		for (int i = 0; i < SIZE; i++) {
			Cell c = cells[i][j];
			JsonArray jArrayCell = new JsonArray();
			jArrayCell.add(new JsonPrimitive(c.floor()));
			jArrayCell.add(new JsonPrimitive(c.object()));
			ItemCollection cellItems = items.get(i * 100000 + j);
			if (cellItems != null) {
				jArrayCell.add(cellItems.serialize(context));
			}
			jArray.add(jArrayCell);
		}
	}
	return jArray;
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
}
