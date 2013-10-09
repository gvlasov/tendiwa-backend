package tendiwa.core;

import tendiwa.geometry.DSL;
import tendiwa.geometry.WorldRectangleBuilder;

import java.util.HashSet;

import static tendiwa.geometry.DSL.worldBuilder;

public class World {

private final HorizontalPlane defaultPlane = new HorizontalPlane();
protected final int width;
protected final int height;
private HashSet<LocationPlace> locations = new HashSet<>();

public World(int width, int height) {
	this.width = width;
	this.height = height;
}
protected void setLocationPlaces(Iterable<LocationPlace> places) {
	for (LocationPlace place : places) {
		locations.add(place);
	}
}
private void checkIfLocationPlacesFillAllWorld() {
	boolean filled[][] = new boolean[width][height];
	for (LocationPlace location : locations) {
		for (int x=0; x<location.width; x++) {
			for (int y=0; y<location.width; y++) {
				filled[x][y] = true;
			}
		}
	}
	for (int x=0; x<width; x++) {
		for (int y=0; y<width; y++) {
			if (!filled[x][y]) {
				throw new WorldException("Not the whole world was filled with locations");
			}
		}
	}
}

public static World create(WorldDrawer worldDrawer, int width, int height) {
	WorldRectangleBuilder builder = worldBuilder();
	worldDrawer.draw(builder, width, height);
	World world = new World(width, height);
	for (LocationPlace place : builder.getLocationPlaces())	 {
		LocationDrawer locationDrawer = ResourcesRegistry.getLocationDrawerFor(place);
		locationDrawer.draw(new Location(world.defaultPlane, place.x, place.y, place.width, place.height), place);
	}
	return world;
}


}
