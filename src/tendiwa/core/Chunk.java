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

void addCharacter(PlayerCharacter ch, Portal portal) {
	/**
	 * Adds character near portal. Portal is portal object not in this
	 * location, but in location character came from.
	 */
	Coordinate spawn = portal.getAnotherEnd();
	boolean freeSpaceFound = false;
	both:
	for (int dx = -1; dx < 2; dx++) {
		/**
		 * Search for free space near portal
		 */
		for (int dy = -1; dy < 2; dy++) {
			if (cells[spawn.x + dx][spawn.y + dy].getPassability() == PASSABILITY_FREE) {
				spawn.move(spawn.x + dx, spawn.y + dy);
				freeSpaceFound = true;
				break both;
			}
		}
	}
	if (!freeSpaceFound) {
		throw new Error("Free space not found");
	}
	cells[spawn.x][spawn.y].character(ch);
	ch.x = spawn.x;
	ch.y = spawn.y;
	characters.add(ch);
}

void removeCharacter(Character character) {
	cells[character.x - x][character.y - y].setPassability(PASSABILITY_FREE);
	cells[character.x - x][character.y - y].character(false);
	characters.remove(character);
}

public void setFloor(int x, int y, int type) {
	super.setFloor(x, y, type);
	timeStream.fireEvent(ServerEvents.create("floorChange", "[" + type + "," + (this.x + x) + "," + (this.y + y) + "]"));
}

public void setObject(int x, int y, int type) {
	super.setObject(x, y, type);
	timeStream.fireEvent(ServerEvents.create("objectAppear", "[" + type + "," + (this.x + x) + "," + (this.y + y) + "]"));
	for (NonPlayerCharacter ch : nonPlayerCharacters) {
		if (ch.initialCanSee(x, y)) {
			ch.getVisibleEntities();
		}
	}
}

public void setObject(Coordinate c, int type) {
	setObject(c.x, c.y, type);
}

public void removeObject(int x, int y) {
	super.removeObject(x, y);
	timeStream.fireEvent(ServerEvents.create("objectDisappear", "[" + (this.x + x) + "," + (this.y + y) + "]"));
	for (NonPlayerCharacter ch : nonPlayerCharacters) {
		if (ch.initialCanSee(x, y)) {
			ch.getVisibleEntities();
		}
	}
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
	timeStream.fireEvent(ServerEvents.create("itemAppear", "[" + item.getType().getId() + "," + item.id + "," + (this.x + x) + "," + (this.y + y) + "]"));
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
	timeStream.fireEvent(ServerEvents.create("itemDisappear", "[" + pile.getType().getId() + "," + pile.getAmount() + "," + (this.x + x) + "," + (this.y + y) + "]"));
}

public void removeItem(ItemPile pile, int x, int y) {
	super.removeItem(pile, x, y);
	timeStream.fireEvent(ServerEvents.create("itemDisappear", "[" + pile.getType().getId() + "," + pile.getAmount() + "," + (this.x + x) + "," + (this.y + y) + "]"));
}

public void removeItem(UniqueItem item, int x, int y) {
	super.removeItem(item, x, y);
	timeStream.fireEvent(ServerEvents.create("itemDisappear", "[" + item.getType().getId() + "," + item.id + "," + (this.x + x) + "," + (this.y + y) + "]"));
}

void setCharacter(int x, int y, int characterTypeId, int fraction) {
	createCharacter(x, y, characterTypeId, "", 0);
}

public void createSoundSource(int x, int y, SoundType type) {
	soundSources.add(new SoundSource(x, y, type, 1000));
	timeStream.fireEvent(ServerEvents.create("soundSourceAppear", "[" + type.getId() + "," + (this.x + x) + "," + (this.y + y) + "]"));
}

public void removeSoundSource(int x, int y) {
	int size = soundSources.size();
	for (int i = 0; i < size; i++) {
		Sound s = soundSources.get(i);
		if (s.x == x && s.y == y) {
			soundSources.remove(i);
			timeStream.fireEvent(ServerEvents.create("soundSourceDisppear", "[" + 1 + "," + (this.x + x) + "," + (this.y + y) + "]"));
			return;
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
