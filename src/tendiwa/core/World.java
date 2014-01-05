package tendiwa.core;

import java.util.HashMap;
import java.util.Set;

import static tendiwa.core.DSL.worldBuilder;

public class World {

private static final int defaultPlaneIndex = Integer.MAX_VALUE / 2;
protected final int width;
protected final int height;
final HorizontalPlane defaultPlane;
private Character playerCharacter;
private TimeStream timeStream;
private HashMap<Integer, HorizontalPlane> planes = new HashMap<>();

public World(int width, int height) {
	this.width = width;
	this.height = height;
	defaultPlane = initPlane(0);
	planes.put(0, defaultPlane);
	this.timeStream = new TimeStream();
}

public static World create(WorldDrawer worldDrawer, int width, int height) {
	WorldRectangleBuilder builder = worldBuilder();
	worldDrawer.drawWorld(builder, width, height);
	World world = new World(width, height);
	builder.done();
	for (LocationPlace place : builder.rectanglesToPlaces.values()) {
		LocationDrawer locationDrawer = ResourcesRegistry.getLocationDrawerFor(place);
		locationDrawer.draw(
			new Location(world.defaultPlane, place.x, place.y, place.width, place.height),
			place
		);
	}
//	world.checkIfLocationPlacesFillAllWorld(builder);
	return world;
}

public HorizontalPlane initPlane(int level) {
	HorizontalPlane plane = new HorizontalPlane(width, height, this, level);
	plane.touchChunks(0, 0, width, height);
	return plane;
}

public HorizontalPlane getDefaultPlane() {
	return defaultPlane;
}

private void checkIfLocationPlacesFillAllWorld(WorldRectangleBuilder builder) {
	boolean filled[][] = new boolean[width][height];
	for (LocationPlace place : builder.rectanglesToPlaces.values()) {
		for (int x = place.x; x < place.width; x++) {
			for (int y = place.y; y < place.height; y++) {
				filled[x][y] = true;
			}
		}
	}
	for (int x = 0; x < width; x++) {
		for (int y = 0; y < height; y++) {
			if (!filled[x][y]) {
				throw new WorldException("Not the whole world was filled with locations: " + x + " " + y);
			}
		}
	}
}

public Character getPlayer() {
	assert playerCharacter != null;
	return playerCharacter;
}

public void setPlayerCharacter(Character playerCharacter) {
	this.playerCharacter = playerCharacter;
	timeStream.addPlayerCharacter(playerCharacter);
	playerCharacter.setTimeStream(timeStream);
}

public int getWidth() {
	return width;
}

public int getHeight() {
	return height;
}

/**
 * Creates a new {@link Character}, makes it the player character.
 *
 * @param x
 * 	X coordinate of player in world coordinates. Will be shifted to the east if {x:y} is occupied by something
 * 	non-passable.
 * @param y
 * 	Y coordinate of player in world coordinates
 * @param type
 * 	Player's speices.
 * @param name
 * 	Name of a player.
 * @return A new Character which is set to be World's player character.
 * @throws RuntimeException
 * 	If the cell {x:y} was not passable and neither were all the cells from it till {width-1:y}
 */
public Character createPlayerCharacter(int x, int y, CharacterType type, String name) {
	playerCharacter = new Character(defaultPlane, type, x, y, name);
	try {
		while (defaultPlane.getPassability(playerCharacter.x, playerCharacter.y) == Passability.NO) {
			playerCharacter.x++;
		}
	} catch (ArrayIndexOutOfBoundsException e) {
		throw new RuntimeException("Could not place player character because the whole world is non-passable");
	}
	defaultPlane.addCharacter(playerCharacter);
	timeStream.addPlayerCharacter(playerCharacter);
	Set<Chunk> chunks = defaultPlane.getChunksAroundCoordinate(x, y, Chunk.SIZE * 5);
	for (Chunk chunk : chunks) {
		timeStream.addChunk(chunk);
	}
	return playerCharacter;
}

public TimeStream getTimeStream() {
	return timeStream;
}

/**
 * Lazily returns a HorizontalPlane with index {@code level}. Planes stack on top of each other, with default plane
 * having index 0. If plane with index {@code level} doesn't exist, this method creates that plane. If it does exist, an
 * existing plane is returned. However, to create a plane with index {@code level}, a plane with index {@code level-1}
 * must exist.
 *
 * @param level
 * 	Index of plane to retrieve.
 * @return An existing plane or a new plane, if a plane with that index doesn't exist.
 */
public HorizontalPlane getPlane(int level) {
	if (planes.get(level) == null) {
		if (planes.get(level - 1) == null) {
			throw new IllegalArgumentException("Can't create plane " + level + " because plane " + (level - 1) + " doesn't exist yet");
		}
		HorizontalPlane newPlane = initPlane(level);
		planes.put(level, newPlane);
		return newPlane;
	} else {
		return planes.get(level);
	}
}
}
