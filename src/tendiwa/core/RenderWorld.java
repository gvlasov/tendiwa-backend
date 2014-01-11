package tendiwa.core;

import java.util.HashMap;
import java.util.Map;

public class RenderWorld {
Map<Integer, RenderPlane> planes = new HashMap<>();

public RenderPlane createPlane(int zLevel) {
	RenderPlane value = new RenderPlane(Tendiwa.getWorld().getPlane(zLevel));
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
