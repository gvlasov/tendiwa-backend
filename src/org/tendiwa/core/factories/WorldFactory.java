package org.tendiwa.core.factories;

import com.google.inject.Inject;
import org.tendiwa.core.*;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;

public class WorldFactory {

private final TimeStreamFactory timeStreamFactory;
private final PlayerCharacterProvider playerCharacterProvider;

@Inject
public WorldFactory(TimeStreamFactory timeStreamFactory, PlayerCharacterProvider playerCharacterProvider) {
	this.timeStreamFactory = timeStreamFactory;
	this.playerCharacterProvider = playerCharacterProvider;
}

public World create(WorldDrawer worldDrawer, int width, int height) {
	WorldRectangleBuilder builder = DSL.worldBuilder();
	worldDrawer.drawWorld(builder, width, height);
	World world = new World(width, height, timeStreamFactory, playerCharacterProvider);
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
