package org.tendiwa.core;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.events.EventFovChange;
import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.events.EventMoveToPlane;
import org.tendiwa.core.observation.EventEmitter;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.Observer;

import java.util.HashMap;
import java.util.Map;

public class RenderWorld {
private final World world;
Map<Integer, RenderPlane> planes = new HashMap<>();
private RenderPlane currentPlane;

@Inject
RenderWorld(@Named("tendiwa") Observable model, @Named("current_player_world") final World world) {
	this.world = world;
	model.subscribe(new Observer<EventFovChange>() {
		@Override
		public void update(EventFovChange event, EventEmitter<EventFovChange> emitter) {
			getCurrentPlane().updateFieldOfView(event);
		}
	}, EventFovChange.class);
	model.subscribe(new Observer<EventInitialTerrain>() {
		@Override
		public void update(EventInitialTerrain event, EventEmitter<EventInitialTerrain> emitter) {
			setCurrentPlane(world.getPlane(event.zLevel));
			getCurrentPlane().initFieldOfView(event);
		}
	}, EventInitialTerrain.class);
	model.subscribe(new Observer<EventMoveToPlane>() {

		@Override
		public void update(EventMoveToPlane event, EventEmitter<EventMoveToPlane> emitter) {
			getCurrentPlane().unseeAllCells();
			setCurrentPlane(world.getPlane(event.zLevel));
			for (RenderCell cell : event.seenCells) {
				getCurrentPlane().seeCell(cell);
				if (getCurrentPlane().hasAnyUnseenItems(cell.x, cell.y)) {
					getCurrentPlane().removeUnseenItems(cell.x, cell.y);
				}
			}

		}
	}, EventMoveToPlane.class);

}

public RenderPlane createPlane(int zLevel) {
	RenderPlane value = new RenderPlane(world, world.getPlane(zLevel));
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
