package org.tendiwa.core;

import java.util.HashMap;
import java.util.Map;

public class RenderWorld {
private final World world;
Map<Integer, RenderPlane> planes = new HashMap<>();

RenderWorld(World world) {
	this.world = world;
}
public RenderPlane createPlane(int zLevel) {
	RenderPlane value = new RenderPlane(world.getPlane(zLevel));
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
}
