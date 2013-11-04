package tendiwa.core;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * <p> A HorizontalPlane is a single storey of the world, much like a single level of a dungeon in traditional
 * rogue-likes. It is a potentially infinite Cartesian plane with integral coordinates divided by square {@link Chunk}s.
 * No actions can be interplanar (you can't shoot an arrow from one plane to another), however characters can move from
 * one plane to another using stairs, shafts, teleportation or other means. </p> <p> Not to be mistaken with {@link
 * TimeStream} </p> <p> The purpose of HorizontalPlane is to store and access chunks of terrain located on the same
 * absolute height in the world. </p>
 */
public class HorizontalPlane {
protected final HashMap<Integer, HashMap<Integer, Chunk>> chunks = new HashMap<>();
public HorizontalPlane upperPlane;
public HorizontalPlane lowerPlane;
private int numberOfChunks = 0;

public HorizontalPlane() {

}

public Chunk createChunk(int x, int y) {
	if (x % Chunk.SIZE != 0) {
		throw new Error("Wrong x " + x);
	}
	if (y % Chunk.SIZE != 0) {
		throw new Error("Wrong y " + y);
	}
	if (!chunks.containsKey(x)) {
		chunks.put(x, new HashMap<Integer, Chunk>());
	}
	if (chunks.get(x).containsKey(y)) {
		throw new Error("Chunk at " + x + ":" + y + " already exists");
	}
	numberOfChunks++;
	return chunks.get(x).put(y, new Chunk(this, x, y));
}

public void touchChunk(int x, int y) {
	if (!hasChunk(x, y)) {
		createChunk(x, y);
	}
}

public void touchChunks(int x, int y, int width, int height) {
	for (int j = getChunkRoundedCoord(y); j <= y + height; j += Chunk.SIZE) {
		for (int i = getChunkRoundedCoord(x); i <= x + width; i += Chunk.SIZE) {
			touchChunk(i, j);
		}
	}
}

public Chunk getChunkWithCell(int x, int y) {
	int chX = (x < 0) ? x - ((x % Chunk.SIZE == 0) ? 0 : Chunk.SIZE) - x % Chunk.SIZE
		: x - x % Chunk.SIZE;
	int chY = (y < 0) ? y - ((y % Chunk.SIZE == 0) ? 0 : Chunk.SIZE) - y % Chunk.SIZE
		: y - y % Chunk.SIZE;
	try {
		return chunks.get(chX).get(chY);
	} catch (NullPointerException e) {
		throw new NullPointerException("No chunk " + chX + ":" + chY + " with cell " + x + ":" + y);
	}
}

/**
 * Returns all chunks present in this HorizontalPlane.
 *
 * @return All chunks.
 */
public Collection<Chunk> getChunks() {
	Collection<Chunk> chs = new HashSet<>();
	for (HashMap<Integer, Chunk> column : chunks.values()) {
		for (Chunk chunk : column.values()) {
			chs.add(chunk);
		}
	}
	return chs;
}

public Chunk getChunkByCoord(int x, int y) {
	return chunks.get(x).get(y);
}

public boolean hasChunk(int x, int y) {
	return chunks.containsKey(x - x % Chunk.SIZE) && chunks.get(x - x % Chunk.SIZE).containsKey(y - y % Chunk.SIZE);
}

public void placeTrail(Trail trail) {
	final Rectangle pointsArea = EnhancedRectangle.rectangleContainingAllPonts(trail.points);
	pointsArea.x -= trail.width;
	pointsArea.y -= trail.width;
	pointsArea.width += trail.width * 2;
	pointsArea.height += trail.width * 2;
	touchChunks(pointsArea.x, pointsArea.y, pointsArea.width, pointsArea.height);
	trail.draw(new TerrainBasics(pointsArea.x, pointsArea.y) {

		@Override
		public int getWidth() {
			return pointsArea.width;
		}

		@Override
		public int getHeight() {
			return pointsArea.height;
		}
	});
}

public Cell[][] getCells(int x, int y, int width, int height) {
	Cell[][] answer = new Cell[width][height];
	int chunkX = getChunkRoundedCoord(x);
	int chunkY = getChunkRoundedCoord(y);
	// Difference between the start cell and the coordinate of a chunk it is
	// in.
	int endX = x + width;
	int endY = y + height;
	for (int currX = x; currX < endX; chunkX += Chunk.SIZE, currX = chunkX) {
		for (int currY = y; currY < endY; chunkY += Chunk.SIZE, currY = chunkY) {
			// For each chunk in the selected zone
			Chunk chunk = getChunkByCoord(chunkX, chunkY);
			int dxInResult = 0;
			for (int k = currX - chunkX; k < Chunk.SIZE && chunkX + k != endX; k++) {
				// Fill answer array with cells from chunk
				int dyInResult = 0;
				for (int l = currY - chunkY; l < Chunk.SIZE && chunkY + l != endY; l++) {
					answer[currX - x + dxInResult][currY - y + dyInResult++] = chunk.cells[k][l];
				}
				dxInResult++;
			}
		}
		// It IS neccessary!
		chunkY = getChunkRoundedCoord(y);
	}
	return answer;
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

public Cell getCell(int x, int y) {
	return getChunkWithCell(x, y).getCell(x, y);
}

public NonPlayerCharacter createCharacter(int absX, int absY, int characterTypeId, String name, int fraction) {
	Chunk chunk = getChunkWithCell(absX, absY);
	return chunk.createCharacter(absX - chunk.x, absY - chunk.y, characterTypeId, name, fraction);
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
}
