package org.tendiwa.core.factories;

import com.google.inject.Inject;
import org.tendiwa.core.World;
import org.tendiwa.core.WorldDrawer;
import org.tendiwa.core.WorldRectangleBuilder;

public class WorldFactory {

@Inject
public WorldFactory() {
}

public World create(WorldDrawer worldDrawer, int width, int height) {
	WorldRectangleBuilder builder = new WorldRectangleBuilder();
	worldDrawer.drawWorld(builder, width, height);
	World world = new World(width, height);
	builder.done();
	throw new UnsupportedOperationException();
//	for (LocationPlace place : builder.getRectanglesToPlaces().values()) {
//		LocationDrawer locationDrawer = ResourcesRegistry.getLocationDrawerFor(place);
//		locationDrawer.draw(
//			new Location(
//				world.getDefaultPlane(),
//				place.getX(),
//				place.getY(),
//				place.getWidth(),
//				place.getHeight()
//			),
//			place
//		);
//	}
	//	world.checkIfLocationPlacesFillAllWorld(builder);
//	return world;
}

}
