package tendiwa.core;

import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.LocationPlace;
import tendiwa.geometry.WorldRectangleBuilder;

import static tendiwa.geometry.DSL.*;

public class World {

protected final int width;
protected final int height;
final HorizontalPlane defaultPlane = new HorizontalPlane();

public World(int width, int height) {
	this.width = width;
	this.height = height;
	defaultPlane.touchChunks(0, 0, width, height);
}

public static World create(WorldDrawer worldDrawer, int width, int height) {
	WorldRectangleBuilder builder = worldBuilder();
	worldDrawer.draw(builder, width, height);
	World world = new World(width, height);
	for (LocationPlace place : builder.getLocationPlaces()) {
		LocationDrawer locationDrawer = ResourcesRegistry.getLocationDrawerFor(place);
		EnhancedRectangle rectangle = place.getRectangle();
		locationDrawer.draw(
			new Location(world.defaultPlane, rectangle.x, rectangle.y, rectangle.width, rectangle.height),
			place
		);
	}
//	world.checkIfLocationPlacesFillAllWorld(builder);
	return world;
}

private void checkIfLocationPlacesFillAllWorld(WorldRectangleBuilder builder) {
	boolean filled[][] = new boolean[width][height];
	for (LocationPlace location : builder.getLocationPlaces()) {
		EnhancedRectangle r = location.getRectangle();
		for (int x = r.x; x < r.width; x++) {
			for (int y = r.y; y < r.height; y++) {
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

public Cell[][] getCellContents() {
	return defaultPlane.getCells(0, 0, width, height);
}
}
