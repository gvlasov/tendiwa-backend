package org.tendiwa.core;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;
import org.tendiwa.core.events.EventFovChange;
import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.events.EventMoveToPlane;
import org.tendiwa.core.factories.RenderPlaneFactory;
import org.tendiwa.core.observation.*;

import java.util.HashMap;
import java.util.Map;

public class RenderWorld {
private final World world;
private final RenderPlaneFactory renderPlaneFactory;
Map<Integer, RenderPlane> planes = new HashMap<>();
private RenderPlane currentPlane;

@Inject
RenderWorld(
	ThreadProxy model,
	@Named("current_player_world") final World world,
	RenderPlaneFactory renderPlaneFactory,
	PlayerCharacterProvider playerCharacterProvider
) {
	this.world = world;
	this.renderPlaneFactory = renderPlaneFactory;
	setCurrentPlane(playerCharacterProvider.get().getPlane());
	model.subscribe(new Observer<EventFovChange>() {
		@Override
		public void update(EventFovChange event, Finishable<EventFovChange> emitter) {
			getCurrentPlane().updateFieldOfView(event);
			emitter.done(this);
		}
	}, EventFovChange.class);
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, Finishable<EventInitialTerrain> emitter) {
			setCurrentPlane(world.getPlane(event.zLevel));
			getCurrentPlane().initFieldOfView(event);
			emitter.done(this);
		}
	}, EventInitialTerrain.class);
	model.subscribe(new Observer<EventMoveToPlane>() {
		@Override
		public void update(EventMoveToPlane event, Finishable<EventMoveToPlane> emitter) {
			getCurrentPlane().unseeAllCells();
			setCurrentPlane(world.getPlane(event.zLevel));
			for (RenderCell cell : event.seenCells) {
				getCurrentPlane().seeCell(cell);
				if (getCurrentPlane().hasAnyUnseenItems(cell.x, cell.y)) {
					getCurrentPlane().removeUnseenItems(cell.x, cell.y);
				}
			}
			emitter.done(this);

		}
	}, EventMoveToPlane.class);
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, Finishable<EventInitialTerrain> emitter) {
			setCurrentPlane(event.player.getPlane());
			emitter.done(this);
		}
	}, EventInitialTerrain.class);
}

public RenderPlane createPlane(int zLevel) {
	RenderPlane value = renderPlaneFactory.create(world, world.getPlane(zLevel));
	planes.put(zLevel, value);
	return value;
}

public RenderPlane touchPlane(int zLevel) {
	if (!planes.containsKey(zLevel)) {
		return createPlane(zLevel);
	}
	return getPlane(zLevel);
}

public RenderPlane getPlane(int zLevel) {
	assert planes.containsKey(zLevel);
	return planes.get(zLevel);
}

public RenderPlane getCurrentPlane() {
	return currentPlane;
}

public void setCurrentPlane(HorizontalPlane plane) {
	currentPlane = touchPlane(plane.getLevel());
}
}
