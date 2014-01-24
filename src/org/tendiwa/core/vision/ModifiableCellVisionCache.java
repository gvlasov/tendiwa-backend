package org.tendiwa.core.vision;

import org.tendiwa.core.meta.CellPosition;

public class ModifiableCellVisionCache extends CellVisionCache {
private final CellPosition character;
boolean visionCacheWritingEnabled = true;
boolean isVisionCacheEmpty = true;

ModifiableCellVisionCache(CellPosition character) {
	this.character = character;
}

static boolean isVisionValue(byte vision) {
	return vision >= Seer.VISION_NOT_COMPUTED && vision <= Seer.VISION_INVISIBLE;
}

void cacheVision(int x, int y, byte vision) {
	assert isVisionValue(vision);
	if (!visionCacheWritingEnabled) {
		return;
	}
	visionCache[(byte) (x - character.getX() + Seer.VISION_RANGE)][(byte) (y - character.getY() + Seer.VISION_RANGE)] = vision;
	isVisionCacheEmpty = false;
}

/**
 * Player's vision cache gets invalidated in {@link Seer#computeFullVisionCache()}.
 */
public void invalidate() {
	for (byte i = 0; i < VISION_CACHE_WIDTH; i++) {
		for (byte j = 0; j < VISION_CACHE_WIDTH; j++) {
			visionCache[i][j] = Seer.VISION_NOT_COMPUTED;
		}
	}
	isVisionCacheEmpty = true;
}

byte getVisionFromCache(int x, int y) {
	return visionCache[(byte) (x - character.getX() + Seer.VISION_RANGE)][(byte) (y - character.getY() + Seer.VISION_RANGE)];
}

public void disableWriting() {
	visionCacheWritingEnabled = false;
}

public void enableWriting() {
	visionCacheWritingEnabled = true;
}

public void storeTo(CellVisionCache visionPrevious) {
	for (int i = 0; i < VISION_CACHE_WIDTH; i++) {
		System.arraycopy(visionCache[i], 0, visionPrevious.visionCache[i], 0, VISION_CACHE_WIDTH);
	}
}

public boolean isVisionCacheEmpty() {
	return isVisionCacheEmpty;
}
}
