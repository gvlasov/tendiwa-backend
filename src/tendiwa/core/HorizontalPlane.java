package tendiwa.core;

import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import tendiwa.geometry.RectangleArea;



/**
 * <p>
 * A HorizontalPlane is a single storey of the world, much like a single level
 * of a dungeon in traditional rogue-likes. It is a potentially infinite
 * Cartesian plane with integral coordinates divided by square {@link Chunk}s.
 * No actions can be interplanar (you can't shoot an arrow from one plane to
 * another), however characters can move from one plane to another using stairs,
 * shafts, teleportation or other means.
 * </p>
 * <p>
 * Not to be mistaken with {@link TimeStream}
 * </p>
 * <p>
 * The purpose of HorizontalPlane is to store and access chunks of terrain
 * located on the same absolute height in the world.
 * </p>
 */
public class HorizontalPlane {
	public HorizontalPlane upperPlane;
	public HorizontalPlane lowerPlane;
	private int numberOfChunks = 0;
	protected final HashMap<Integer, HashMap<Integer, Chunk>> chunks = new HashMap<Integer, HashMap<Integer, Chunk>>();

	public HorizontalPlane() {

	}

	public Chunk createChunk(int x, int y) {
		if (x % Chunk.WIDTH != 0) {
			throw new Error("Wrong x " + x);
		}
		if (y % Chunk.WIDTH != 0) {
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
		for (int j = getChunkRoundedCoord(y); j <= y + height; j += Chunk.WIDTH) {
			for (int i = getChunkRoundedCoord(x); i <= x + width; i += Chunk.WIDTH) {
				touchChunk(i, j);
			}
		}
	}

	public Chunk getChunkWithCell(int x, int y) {
		int chX = (x < 0) ? x - ((x % Chunk.WIDTH == 0) ? 0 : Chunk.WIDTH) - x % Chunk.WIDTH
			: x - x % Chunk.WIDTH;
		int chY = (y < 0) ? y - ((y % Chunk.WIDTH == 0) ? 0 : Chunk.WIDTH) - y % Chunk.WIDTH
			: y - y % Chunk.WIDTH;
		try {
			return chunks.get(chX).get(chY);
		} catch (NullPointerException e) {
			System.out.println("No chunk " + chX + ":" + chY + " with cell " + x + ":" + y);
			return null;
		}
	}
	/**
	 * Returns all chunks present in this HorizontalPlane.
	 * @return
	 */
	public Collection<Chunk> getChunks() {
		Collection<Chunk> chs = new HashSet<Chunk>();
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
		return chunks.containsKey(x - x % Chunk.WIDTH) && chunks.get(x - x % Chunk.WIDTH).containsKey(y - y % Chunk.WIDTH);
	}

	public Location generateLocation(int x, int y, int width, int height, Class<? extends Location> locationCls) {
		// Create new chunks
		touchChunks(x, y, width, height);
		Location location = null;
		try {
			Constructor<? extends Location> ctor = (Constructor<? extends Location>) locationCls.getDeclaredConstructors()[0];
			location = ctor.newInstance(this, x, y, width, height);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
		return location;
	}
	public void placeTrail(Trail trail) {
		final Rectangle pointsArea = RectangleArea.rectangleContainingAllPonts(trail.points);
		pointsArea.x -= trail.width;
		pointsArea.y -= trail.width;
		pointsArea.width += trail.width*2;
		pointsArea.height += trail.width*2;
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
		for (int currX = x; currX < endX; chunkX += Chunk.WIDTH, currX = chunkX) {
			for (int currY = y; currY < endY; chunkY += Chunk.WIDTH, currY = chunkY) {
				// For each chunk in the selected zone
				Chunk chunk = getChunkByCoord(chunkX, chunkY);
				int dxInResult = 0;
				for (int k = currX - chunkX; k < Chunk.WIDTH && chunkX + k != endX; k++) {
					// Fill answer array with cells from chunk
					int dyInResult = 0;
					for (int l = currY - chunkY; l < Chunk.WIDTH && chunkY + l != endY; l++) {
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
	 * Round a coordinate (x or y, works equal) down to the nearest value in
	 * which may be a corner of a chunk.
	 * 
	 * @param x
	 *            x or y coordinate.
	 * @return Rounded coordinate value.
	 */
	public int getChunkRoundedCoord(int coord) {
		return (coord < 0) ? coord - ((coord % Chunk.WIDTH == 0) ? 0
			: Chunk.WIDTH) - coord % Chunk.WIDTH : coord - coord % Chunk.WIDTH;
	}


	public Cell getCell(int x, int y) {
		return getChunkWithCell(x, y).getCell(x, y);
	}


	public void openDoor(int x, int y) {
		Chunk chunk = getChunkWithCell(x, y);
		int doorId = chunk.getCell(x, y).object();
		chunk.removeObject(x - chunk.x, y - chunk.y);
		if (doorId % 2 == 0) {
			// The door is closed, open the door
			chunk.setObject(x - chunk.x, y - chunk.y, doorId - 1);
		} else {
			chunk.setObject(x - chunk.x, y - chunk.y, doorId + 1);
		}
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
}
