package org.tendiwa.events;

import tendiwa.core.Character;
import tendiwa.core.RenderCell;
import tendiwa.core.Tendiwa;

import java.util.LinkedList;

public class EventFovChange implements Event {
public final LinkedList<RenderCell> seen = new LinkedList<>();
public final LinkedList<Integer> unseen = new LinkedList<>();

/**
 * @param xPrev
 * 	X coordinate of {@link tendiwa.core.PlayerCharacter} on previous turn.
 * @param yPrev
 * 	Y coordinate of {@link tendiwa.core.PlayerCharacter} on previous turn.
 * @param visionPrevious
 * 	Vision cache of PlayerCharacter on previous turn.
 * @param visionCurrent
 * 	Vision cache of PlayerCharacter on current turn.
 */
public EventFovChange(int xPrev, int yPrev, byte[][] visionPrevious, byte[][] visionCurrent) {
	int worldHeight = Tendiwa.getWorld().getHeight();
	int dx = Tendiwa.getPlayer().getX() - xPrev;
	int dy = Tendiwa.getPlayer().getY() - yPrev;
	for (int i = 0; i < Character.VISION_RANGE; i++) {
		for (int j = 0; j < Character.VISION_RANGE; j++) {
			boolean pointInNewCacheInsideOld = i - dx >= 0
				&& j - dy >= 0
				&& i - dx < Character.VISION_CACHE_WIDTH
				&& j - dy < Character.VISION_CACHE_WIDTH;
			if (visionPrevious[i][j] == Character.VISION_VISIBLE
				&& pointInNewCacheInsideOld
				&& visionCurrent[i - dx][j - dy] == Character.VISION_INVISIBLE
				|| visionPrevious[i][j] == Character.VISION_VISIBLE
				&& !pointInNewCacheInsideOld
				) {
				unseen.add((xPrev - Character.VISION_RANGE + i) * worldHeight + yPrev - Character.VISION_RANGE + j);
			} else if (visionPrevious[i][j] == Character.VISION_INVISIBLE
				&& pointInNewCacheInsideOld
				&& visionCurrent[i - dx][j - dy] == Character.VISION_VISIBLE
				) {
				int x = xPrev - Character.VISION_RANGE + i;
				int y = yPrev - Character.VISION_RANGE + j;
				seen.add(new RenderCell(x, y, Tendiwa.getWorld().getDefaultPlane().getTerrainElement(x, y)));
			}
		}
	}
	System.out.println(unseen);
}

}
