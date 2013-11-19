package org.tendiwa.events;

import tendiwa.core.Character;
import tendiwa.core.RenderCell;
import tendiwa.core.Tendiwa;

import java.util.LinkedList;

public class EventInitialTerrain implements Event {
public final LinkedList<RenderCell> seen = new LinkedList<>();

/**
 * Answer to initial request after World has just been loaded. Collects terrain around PlayerCharacter to send it to
 * client for displaying.
 */
public EventInitialTerrain() {
	Tendiwa.getPlayer().computeFullVisionCache();
	byte[][] visionCache = Tendiwa.getPlayer().getVisionCache();
	for (int i = 0; i < Character.VISION_CACHE_WIDTH; i++) {
		for (int j = 0; j < Character.VISION_CACHE_WIDTH; j++) {
			if (visionCache[i][j] == Character.VISION_VISIBLE) {
				int x = Tendiwa.getPlayer().getX() - Character.VISION_RANGE + i;
				int y = Tendiwa.getPlayer().getY() - Character.VISION_RANGE + j;
				seen.add(new RenderCell(
					x,
					y,
					Tendiwa.getWorld().getDefaultPlane().getFloor(x, y),
					Tendiwa.getWorld().getDefaultPlane().getWall(x, y)
				));
			}
		}
	}
}
}
