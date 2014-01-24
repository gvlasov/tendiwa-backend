package org.tendiwa.core;

import org.tendiwa.core.vision.ModifiableCellVisionCache;
import org.tendiwa.core.vision.Seer;

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
	Seer seer = Tendiwa.getPlayerCharacter().getSeer();
	byte[][] visionCache = seer.getVisionCache().getContent();
	EnhancedPoint startPoint = seer.getVisionRecStartPoint();
	for (int i = 0; i < ModifiableCellVisionCache.VISION_CACHE_WIDTH; i++) {
		for (int j = 0; j < ModifiableCellVisionCache.VISION_CACHE_WIDTH; j++) {
			if (visionCache[i][j] == Seer.VISION_VISIBLE) {
				int x = startPoint.x + i;
				int y = startPoint.y + j;
				seenCells.add(new RenderCell(
					x,
					y,
					plane.getFloor(x, y),
					plane.getGameObject(x, y)
				));
			}
		}
	}
	EnhancedRectangle actualVisionRec = seer.getVisionRectangle();
	int actualWorldStartX = actualVisionRec.getX();
	int actualWorldStartY = actualVisionRec.getY();
	int actualWorldEndX = actualVisionRec.getMaxX();
	int actualWorldEndY = actualVisionRec.getMaxY();

	for (int i = actualWorldStartX; i < actualWorldEndX; i++) {
		for (int j = actualWorldStartY; j < actualWorldEndY; j++) {
			BorderObject borderObjectW = plane.getBorderObject(i + 1, j, Directions.W);
			boolean cellIsVisible = visionCache[i - startPoint.x][j - startPoint.y] == Seer.VISION_VISIBLE;
			if (cellIsVisible
				&& visionCache[i - startPoint.x + 1][j - startPoint.y] == Seer.VISION_VISIBLE
				) {
				seenBorders.add(new RenderBorder(i + 1, j, Directions.W, borderObjectW));
			}
			BorderObject borderObjectN = plane.getBorderObject(i, j + 1, Directions.N);
			if (cellIsVisible
				&& visionCache[i - startPoint.x][j - startPoint.y + 1] == Seer.VISION_VISIBLE
				) {
				seenBorders.add(new RenderBorder(i, j + 1, Directions.N, borderObjectN));
			}
		}
	}
}

}
