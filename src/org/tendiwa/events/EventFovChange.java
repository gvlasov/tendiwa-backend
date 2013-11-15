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
 * 	Vision cache of PlayerCharacter on previous turn. {@code visionCurrent[Character.VISION_RANGE][Character.VISION_RANGE]}
 * 	is the point Character was standing at on previous turn.
 * @param visionCurrent
 * 	Vision cache of PlayerCharacter on current turn. {@code visionCurrent[Character.VISION_RANGE][Character.VISION_RANGE]}
 * 	is the point Character is standing on current turn.
 */
public EventFovChange(int xPrev, int yPrev, byte[][] visionPrevious, byte[][] visionCurrent) {
	int worldHeight = Tendiwa.getWorld().getHeight();
	int dx = Tendiwa.getPlayer().getX() - xPrev;
	int dy = Tendiwa.getPlayer().getY() - yPrev;
	// Loop over points in previous cache
	for (int i = 0; i < Character.VISION_CACHE_WIDTH; i++) {
		for (int j = 0; j < Character.VISION_CACHE_WIDTH; j++) {
			boolean pointIsInBothCaches = i - dx >= 0
				&& j - dy >= 0
				&& i - dx < Character.VISION_CACHE_WIDTH
				&& j - dy < Character.VISION_CACHE_WIDTH;
			if (pointIsInBothCaches) {
				if (visionPrevious[i][j] == Character.VISION_VISIBLE
					&& visionCurrent[i - dx][j - dy] == Character.VISION_INVISIBLE
					) {
					// If a point was known to be visible, and now it is invisible, then it is unseen
					int x = xPrev - Character.VISION_RANGE + i;
					int y = yPrev - Character.VISION_RANGE + j;
					unseen.add(x * worldHeight + y);
				} else if (visionPrevious[i][j] == Character.VISION_INVISIBLE
					&& visionCurrent[i - dx][j - dy] == Character.VISION_VISIBLE
					) {
					// If a point was known to be invisible, and now it is visible, then it is seen
					int x = xPrev - Character.VISION_RANGE + i;
					int y = yPrev - Character.VISION_RANGE + j;
					seen.add(new RenderCell(x, y, Tendiwa.getWorld().getDefaultPlane().getTerrainElement(x, y)));
				}
			} else {
				// If point is only in the previous cache
				if (visionPrevious[i][j] == Character.VISION_VISIBLE) {
					// If a point was known to be visible, and now it is not known of its visibility,
					// therefore it is invisible and must be unseen
					int x = xPrev - Character.VISION_RANGE + i;
					int y = yPrev - Character.VISION_RANGE + j;
					unseen.add(x * worldHeight + y);
				}
			}
		}
	}
	// Loop over points in the new cache that are _not_ in the previous cache
	for (int i = 0; i < Character.VISION_CACHE_WIDTH; i++) {
		for (int j = 0; j < Character.VISION_CACHE_WIDTH; j++) {
			// Condition from previous loop with reversed dx
			boolean pointIsInBothCaches = i + dx >= 0
				&& j + dy >= 0
				&& i + dx < Character.VISION_CACHE_WIDTH
				&& j + dy < Character.VISION_CACHE_WIDTH;
			if (pointIsInBothCaches) {
				// Points that are in both caches are already computed
				continue;
			}
			if (visionCurrent[i][j] == Character.VISION_VISIBLE) {
				// If it wasn't known of point's visibility, and now it is visible, therefore it was seen.
				int x = xPrev + dx - Character.VISION_RANGE + i;
				int y = yPrev + dy - Character.VISION_RANGE + j;
				seen.add(new RenderCell(x, y, Tendiwa.getWorld().getDefaultPlane().getTerrainElement(x, y)));
			}
		}
	}
}

}