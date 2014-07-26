package org.tendiwa.drawing.extensions;

import org.tendiwa.core.*;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;

public class DrawingWorld {

	public static DrawingAlgorithm<World> defaultAlgorithm() {
		return DrawingWorld.level(0);
	}

	public static DrawingAlgorithm<World> level(final int level) {
		return (world, canvas) -> {
			HorizontalPlane defaultPlane = world.getPlane(level);
			int width = world.getWidth();
			int height = world.getHeight();
			if (width > canvas.getWidth() || height > canvas.getHeight()) {
				throw new RuntimeException("Size of world (" + width + "x" + height + ") is greater than size of canvas (" + width + "x" + height + ")");
			}
			canvas.drawRectangle(new Rectangle(0, 0, width, height), Color.BLACK);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					FloorType floorType = defaultPlane.getFloor(x, y);
					GameObject wallType = defaultPlane.getGameObject(x, y);
					if (defaultPlane.getCharacter(x, y) != null) {
						canvas.drawCell(x, y, Color.YELLOW);
					} else if (defaultPlane.hasAnyItems(x, y)) {
						canvas.drawCell(x, y, Color.ORANGE);
					} else if (defaultPlane.hasObject(x, y)) {
						canvas.drawCell(x, y, Color.PINK);
					} else if (wallType instanceof WallType) {
						// Draw floor
						if (floorType == null) {
							canvas.drawCell(x, y, Color.LIGHT_GRAY);
						} else if (floorType.isLiquid()) {
							canvas.drawCell(x, y, new Color(50, 50, 180));
						} else {
							canvas.drawCell(x, y, Color.GREEN);
						}
					} else {
						// Draw wall
						canvas.drawCell(x, y, Color.GRAY);
					}
				}
			}
		};
	}

	public static DrawingAlgorithm<World> withColorMap(PlaceableToColorMap colorMap) {
		return (world, canvas) -> {
			HorizontalPlane defaultPlane = world.getPlane(0);
			int width = world.getWidth();
			int height = world.getHeight();
			if (width > canvas.getWidth() || height > canvas.getHeight()) {
				throw new RuntimeException("Size of world (" + width + "x" + height + ") is greater than size of canvas (" + width + "x" + height + ")");
			}
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					GameObject gameObject = defaultPlane.getGameObject(x, y);
					if (colorMap.colors.containsKey(gameObject)) {
						canvas.drawCell(x, y, colorMap.colors.get(gameObject));
						continue;
					}
					FloorType floor = defaultPlane.getFloor(x,y);
					if (colorMap.colors.containsKey(floor)) {
						canvas.drawCell(x,y,colorMap.colors.get(floor));
					}
				}
			}
		};
	}
}
