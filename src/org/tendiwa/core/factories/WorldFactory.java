package org.tendiwa.core.factories;

import com.google.inject.Inject;
import org.tendiwa.core.*;

public class WorldFactory {

private final TimeStreamFactory timeStreamFactory;

@Inject
public WorldFactory(TimeStreamFactory timeStreamFactory) {
	this.timeStreamFactory = timeStreamFactory;
}

public World create(WorldDrawer worldDrawer, int width, int height) {
	WorldRectangleBuilder builder = DSL.worldBuilder();
	worldDrawer.drawWorld(builder, width, height);
	World world = new World(width, height, timeStreamFactory);
	builder.done();
	for (LocationPlace place : builder.getRectanglesToPlaces().values()) {
		LocationDrawer locationDrawer = ResourcesRegistry.getLocationDrawerFor(place);
		locationDrawer.draw(
			new Location(
				world.getDefaultPlane(),
				place.getX(),
				place.getY(),
				place.getWidth(),
				place.getHeight()
			),
			place
		);
	}
	//	world.checkIfLocationPlacesFillAllWorld(builder);
	return world;
}

}
