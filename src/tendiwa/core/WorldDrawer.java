package tendiwa.core;

import tendiwa.geometry.WorldRectangleBuilder;

public interface WorldDrawer {
	void draw(WorldRectangleBuilder builder, int width, int height);
}
