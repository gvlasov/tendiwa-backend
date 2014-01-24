package org.tendiwa.core.vision;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.meta.CellPosition;

public class ModifiableBorderVisionCache extends BorderVisionCache {
private static final int BORDER_VISION_CACHE_SIZE = (ModifiableCellVisionCache.VISION_CACHE_WIDTH + 1) * (ModifiableCellVisionCache.VISION_CACHE_WIDTH + 1) * 2;
private final CellPosition character;

ModifiableBorderVisionCache(CellPosition character) {
	this.character = character;
}

void invalidate() {
	int borderCacheSide = ModifiableCellVisionCache.VISION_CACHE_WIDTH * ModifiableCellVisionCache.VISION_CACHE_WIDTH * 2;
	for (int i = 0; i < borderCacheSide; i++) {
		borderVision[i] = Seer.VISION_NOT_COMPUTED;
	}
}

public void storeTo(BorderVisionCache borderVisionPrevious) {
	System.arraycopy(borderVision, 0, borderVisionPrevious.borderVision, 0, BORDER_VISION_CACHE_SIZE);
}

void cacheBorderVision(int x, int y, CardinalDirection side, byte vision) {
	assert !side.isGrowing();
	assert ModifiableCellVisionCache.isVisionValue(vision);
	int index = getBorderCacheIndex(x, y, side);
	borderVision[index] = vision;
}

private int getBorderCacheIndex(int x, int y, CardinalDirection side) {
	return (side == Directions.N ? 1 : 0) + (y - character.getY() + Seer.VISION_RANGE) * 2 + (x - character.getX() + Seer.VISION_RANGE) * (ModifiableCellVisionCache.VISION_CACHE_WIDTH + 1) * 2;
}
}
