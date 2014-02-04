package org.tendiwa.core.factories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.*;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;

public class WorldFactory {
private final PlayerCharacterProvider playerCharacterProvider;
private final TimeStream timeStream;

@Inject
public WorldFactory(
	PlayerCharacterProvider playerCharacterProvider,
	@Named("player_time_stream") TimeStream timeStream
) {
	this.playerCharacterProvider = playerCharacterProvider;
	this.timeStream = timeStream;
}

public World create(WorldDrawer worldDrawer, int width, int height) {
	WorldRectangleBuilder builder = DSL.worldBuilder();
	worldDrawer.drawWorld(builder, width, height);
	World world = new World(timeStream, playerCharacterProvider, width, height);
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
