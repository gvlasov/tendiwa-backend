package tendiwa.core;

public class Cell {
protected short floor;
protected short object;
Character character = null;
private byte passability = TerrainBasics.Passability.FREE.value();

public Cell() {

}

public Cell(short f, short o, Character ch) {
	this();
	floor = f;
	object = o;
	character = ch;
}

public Cell(short f, short o) {
	this();
	floor = f;
	object = o;
}

public Cell(Cell cell) {
	floor = cell.floor;
	object = cell.object;
	character = cell.character;
}

public Character character() {
	return character;
}

public boolean hasCharacter() {
	return character != null;
}

public void character(boolean f) {
	character = null;
}

public void character(Character ch) {
	character = ch;
}

public int floor() {
	return floor;
}

public void floor(short value) {
	floor = value;
}

public short object() {
	return object;
}

public void object(short value) {
	object = value;
}

public boolean isDoor() {
	return object > 40 && object < 51;
}

public void setPassability(byte passability) {
	this.passability = passability;
}

/**
 * Returns object representation of cell's passability.
 *
 * @return Type of cell's passability.
 */
public TerrainBasics.Passability getPassability() {
	return TerrainBasics.Passability.value2Passability(passability);
}

public void setPassability(TerrainBasics.Passability passability) {
	this.passability = passability.value();
}

/**
 * Returns byte representation of cell's passability. This method is much quicker, but less convenient than {@link
 * tendiwa.core.Cell#getPassability()}.
 *
 * @return Type of cell's passability.
 * @see tendiwa.core.Cell#getPassability()
 */
public byte getPassabilityByte() {
	return passability;
}

public boolean contains(PlaceableInCell placeable) {
	return placeable.containedIn(this);
}
}
