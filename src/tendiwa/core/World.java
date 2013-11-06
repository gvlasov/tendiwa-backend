package tendiwa.core;

import org.tendiwa.events.EventFovChange;
import org.tendiwa.events.EventInitialTerrain;

import static tendiwa.core.DSL.worldBuilder;

public class World {

protected final int width;
protected final int height;
final HorizontalPlane defaultPlane;
private PlayerCharacter playerCharacter;

public World(int width, int height) {
	this.width = width;
	this.height = height;
	defaultPlane = new HorizontalPlane(width, height);
	defaultPlane.touchChunks(0, 0, width, height);
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

public PlayerCharacter getPlayerCharacter() {
	assert playerCharacter != null;
	return playerCharacter;
}

public void setPlayerCharacter(PlayerCharacter playerCharacter) {
	while (defaultPlane.getPassability(playerCharacter.x, playerCharacter.y) == Chunk.Passability.NO) {
		playerCharacter.x++;
	}
	this.playerCharacter = playerCharacter;
}

public void placePlayerCharacter(PlayerCharacter player, int x, int y) {
	defaultPlane.placeCharacter(player, x, y);
}

public int getWidth() {
	return width;
}

public int getHeight() {
	return height;
}

}
