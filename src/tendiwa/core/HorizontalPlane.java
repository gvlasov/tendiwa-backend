package tendiwa.core;

/**
 * <p> A HorizontalPlane is a single storey of the world, much like a single level of a dungeon in traditional
 * rogue-likes. It is a potentially infinite Cartesian plane with integral coordinates divided by square {@link Chunk}s.
 * No actions can be interplanar (you can't shoot an arrow from one plane to another), however characters can move from
 * one plane to another using stairs, shafts, teleportation or other means. </p> <p> Not to be mistaken with {@link
 * TimeStream} </p> <p> The purpose of HorizontalPlane is to store and access chunks of terrain located on the same
 * absolute height in the world. </p>
 */
public class HorizontalPlane {
protected final Chunk[][] chunks;
private final int width;
private final int height;
public HorizontalPlane upperPlane;
public HorizontalPlane lowerPlane;
private int numberOfChunks = 0;

/**
 * @param width
 * 	Width of plane in cells.
 * @param height
 * 	Height of plane in cells.
 */
public HorizontalPlane(int width, int height) {
	chunks = new Chunk[width / Chunk.SIZE + 1][height / Chunk.SIZE + 1];
	this.width = width;
	this.height = height;
}

public Chunk loadChunk(int x, int y) {
	int chunkX = (x - x % Chunk.SIZE) / Chunk.SIZE;
	int chunkY = (y - y % Chunk.SIZE) / Chunk.SIZE;
	if (chunks[chunkX][chunkY] != null) {
		throw new RuntimeException("Trying to load a chunk that is already loaded.");
	}
	return chunks[chunkX][chunkY] = loadChunkFromFilesystem(chunkX, chunkY);
}

private Chunk loadChunkFromFilesystem(int chunkX, int chunkY) {
	return null;
}

public void touchChunk(int x, int y) {
	if (!hasChunk(x, y)) {
		chunks[x / Chunk.SIZE][y / Chunk.SIZE] = new Chunk(this, x, y);
	}
}

public void touchChunks(int x, int y, int width, int height) {
	for (int j = getChunkRoundedCoord(y); j <= y + height; j += Chunk.SIZE) {
		for (int i = getChunkRoundedCoord(x); i <= x + width; i += Chunk.SIZE) {
			touchChunk(i, j);
		}
	}
}

/**
 * Returns the chunk that contains cell with absolute coordinates x:y. Loads it if it was not loaded.
 *
 * @param x
 * 	Absolute x coordinate of a cell.
 * @param y
 * 	Absolute y coordinate of a cell.
 * @return Chunk that contains a cell with given absolute coordinates.
 */
public Chunk getChunkWithCell(int x, int y) {
	if (x < 0 || y < 0 || x >= width || y >= height) {
		throw new ArrayIndexOutOfBoundsException("Point " + x + ":" + y + " is not inside plane of " + width + "x" + height + " cells large.");
	}
	int chunkX = (x - x % Chunk.SIZE) / Chunk.SIZE;
	int chunkY = (y - y % Chunk.SIZE) / Chunk.SIZE;
	Chunk chunk = chunks[chunkX][chunkY];
	return chunk == null ? loadChunk(x, y) : chunk;
}

public boolean hasChunk(int x, int y) {
	return chunks[(x - x % Chunk.SIZE) / Chunk.SIZE][(y - y % Chunk.SIZE) / Chunk.SIZE] != null;
}

/**
 * Round a coordinate (x or y, works equal) down to the nearest value in which may be a corner of a chunk.
 *
 * @param coord
 * 	x or y coordinate.
 * @return Rounded coordinate value.
 */
public int getChunkRoundedCoord(int coord) {
	return (coord < 0) ? coord - ((coord % Chunk.SIZE == 0) ? 0
		: Chunk.SIZE) - coord % Chunk.SIZE : coord - coord % Chunk.SIZE;
}

public short getTerrainElement(int x, int y) {
	return getChunkWithCell(x, y).getTerrainElement(x, y);
}

public Chunk.Passability getPassability(int x, int y) {
	return TerrainType.getById(getChunkWithCell(x, y).getTerrainElement(x, y)).getPassability();
}

public NonPlayerCharacter createCharacter(int absX, int absY, CharacterType characterType, String name, int fraction) {
	Chunk chunk = getChunkWithCell(absX, absY);
	return chunk.createCharacter(absX - chunk.x, absY - chunk.y, characterType, name, fraction);
}

public void addItem(ItemPile pile, int x, int y) {
	Chunk chunk = getChunkWithCell(x, y);
	chunk.addItem(pile, x - chunk.x, y - chunk.y);
}

public void addItem(UniqueItem item, int x, int y) {
	Chunk chunk = getChunkWithCell(x, y);
	chunk.addItem(item, x - chunk.x, y - chunk.y);
}

public void removeItem(ItemPile pile, int x, int y) {
	Chunk chunk = getChunkWithCell(x, y);
	chunk.removeItem(pile, x - chunk.x, y - chunk.y);
}

public void removeItem(UniqueItem item, int x, int y) {
	Chunk chunk = getChunkWithCell(x, y);
	chunk.removeItem(item, x - chunk.x, y - chunk.y);
}

public ItemCollection getItems(int x, int y) {
	Chunk chunk = getChunkWithCell(x, y);
	return chunk.getItems(x - chunk.x, y - chunk.y);
}

public void placeCharacter(Character character, int x, int y) {
	Chunk chunk = getChunkWithCell(x, y);
	chunk.addCharacter(character);
}

public void removeObject(int x, int y) {
	Chunk chunkWithCell = getChunkWithCell(x, y);
	chunkWithCell.removeObject(x - chunkWithCell.x, y - chunkWithCell.y);
}

public void placeTerrainElement(short id, int x, int y) {
	getChunkWithCell(x, y).setTerrainElement(id, x, y);
}

public Character getCharacter(int x, int y) {

	return getChunkWithCell(x, y).getCharacter(x, y);
}

public void removeCharacter(Character character) {
	getChunkWithCell(character.x, character.y).removeCharacter(character);
}

public void addCharacter(Character character) {
	getChunkWithCell(character.x, character.y).addCharacter(character);
}

public void setTerrainElement(short id, int x, int y) {
	getChunkWithCell(x, y).setTerrainElement(id, x, y);
}

public GameObject getGameObject(int x, int y) {
	return getChunkWithCell(x, y).getGameObject(x, y);
}
}
