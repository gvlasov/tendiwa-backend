package org.tendiwa.core;

import java.lang.*;
import java.util.LinkedList;

public class EventInitialTerrain implements Event {
public final LinkedList<RenderCell> seenCells = new LinkedList<>();
public final LinkedList<RenderBorder> seenBorders = new LinkedList<>();
public final int zLevel;

/**
 * Answer to initial request after World has just been loaded. Collects terrain around PlayerCharacter to send it to
 * client for displaying.
 */
public EventInitialTerrain() {
	HorizontalPlane plane = Tendiwa.getWorld().getPlayer().getPlane();
	zLevel = plane.getLevel();
	byte[][] visionCache = Tendiwa.getPlayerCharacter().getVisionCache();
	int visibilityRecWorldStartX = Tendiwa.getPlayerCharacter().getX() - Character.VISION_RANGE;
	int visibilityRecWorldStartY = Tendiwa.getPlayerCharacter().getY() - Character.VISION_RANGE;
	for (int i = 0; i < Character.VISION_CACHE_WIDTH; i++) {
		for (int j = 0; j < Character.VISION_CACHE_WIDTH; j++) {
			if (visionCache[i][j] == Character.VISION_VISIBLE) {
				int x = visibilityRecWorldStartX + i;
				int y = visibilityRecWorldStartY + j;
				seenCells.add(new RenderCell(
					x,
					y,
					plane.getFloor(x, y),
					plane.getGameObject(x, y)
				));
			}
		}
	}
	int actualWorldStartX = Math.max(0, visibilityRecWorldStartX);
	int actualWorldStartY = Math.max(0, visibilityRecWorldStartY);
	int actualWorldEndX = Math.min(Tendiwa.getWorldWidth() - 1, visibilityRecWorldStartX + Character.VISION_CACHE_WIDTH - 1);
	int actualWorldEndY = Math.min(Tendiwa.getWorldHeight() - 1, visibilityRecWorldStartY + Character.VISION_CACHE_WIDTH - 1);

	for (int i = actualWorldStartX; i < actualWorldEndX; i++) {
		for (int j = actualWorldStartY; j < actualWorldEndY; j++) {
			BorderObject borderObjectW = plane.getBorderObject(i + 1, j, Directions.W);
			boolean cellIsVisible = visionCache[i - visibilityRecWorldStartX][j - visibilityRecWorldStartY] == Character.VISION_VISIBLE;
			if (cellIsVisible
				&& visionCache[i - visibilityRecWorldStartX + 1][j - visibilityRecWorldStartY] == Character.VISION_VISIBLE
				) {
				seenBorders.add(new RenderBorder(i + 1, j, Directions.W, borderObjectW));
			}
			BorderObject borderObjectN = plane.getBorderObject(i, j + 1, Directions.N);
			if (cellIsVisible
				&& visionCache[i - visibilityRecWorldStartX][j - visibilityRecWorldStartY + 1] == Character.VISION_VISIBLE
				) {
				seenBorders.add(new RenderBorder(i, j + 1, Directions.N, borderObjectN));
			}
		}
	}
}
}
