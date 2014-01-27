package org.tendiwa.core.vision;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.meta.CellPosition;

public class ModifiableBorderVisionCache extends BorderVisionCache {
private static final int BORDER_VISION_CACHE_SIZE = (ModifiableCellVisionCache.VISION_CACHE_WIDTH + 1) * (ModifiableCellVisionCache.VISION_CACHE_WIDTH + 1) * 2;

ModifiableBorderVisionCache(CellPosition character) {
	super(character);
}

void invalidate() {
	int borderCacheSide = ModifiableCellVisionCache.VISION_CACHE_WIDTH * ModifiableCellVisionCache.VISION_CACHE_WIDTH * 2;
	for (int i = 0; i < borderCacheSide; i++) {
		borderVision[i] = Visibility.VISIBLE;
	}
}

public void storeTo(BorderVisionCache borderVisionPrevious) {
	System.arraycopy(borderVision, 0, borderVisionPrevious.borderVision, 0, BORDER_VISION_CACHE_SIZE);
}

void cacheBorderVision(int x, int y, CardinalDirection side, Visibility vision) {
	assert !side.isGrowing();
	int index = getBorderCacheIndex(x, y, side);
	borderVision[index] = vision;
}

}
