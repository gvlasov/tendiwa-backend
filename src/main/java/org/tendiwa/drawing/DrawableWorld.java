package org.tendiwa.drawing;

import org.tendiwa.core.*;
import org.tendiwa.drawing.extensions.PlaceableToColorMap;

public final class DrawableWorld implements Drawable {
	private final World world;
	private final PlaceableToColorMap colorMap;

	public DrawableWorld(
		World world,
		PlaceableToColorMap colorMap
	) {

		this.world = world;
		this.colorMap = colorMap;
	}
	@Override
	public void drawIn(Canvas canvas) {
		HorizontalPlane defaultPlane = world.getPlane(0);
		int width = world.getWidth();
		int height = world.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				GameObject gameObject = defaultPlane.getGameObject(x, y);
				if (gameObject != null) {
					ObjectType type = gameObject.getType();
					if (colorMap.containsKey(type)) {
						canvas.drawCell(x, y, colorMap.get(type));
						continue;
					}
				}
				FloorType floor = defaultPlane.getFloor(x, y);
				if (colorMap.containsKey(floor)) {
					canvas.drawCell(x, y, colorMap.get(floor));
				}
			}
		}
	}
}
