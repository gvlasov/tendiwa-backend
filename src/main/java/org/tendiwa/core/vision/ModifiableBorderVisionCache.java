package org.tendiwa.core.vision;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.meta.Cell;

public class ModifiableBorderVisionCache extends BorderVisionCache {
	private Cell lastCenterCoordinates;

	ModifiableBorderVisionCache(Cell character) {
		super(character);
	}


	public void storeTo(BorderVisionCache borderVisionPrevious) {
		borderVisionPrevious.center = lastCenterCoordinates;
		System.arraycopy(borderVision, 0, borderVisionPrevious.borderVision, 0, BORDER_VISION_CACHE_SIZE);
	}


	/**
	 * Puts {@code vision} under x:y:side key.
	 *
	 * @param x
	 * 	X coordinate in world coordinates
	 * @param y
	 * 	Y coordinate in world coordinates
	 * @param side
	 * 	N or W.
	 * @param vision
	 * 	Value to put.
	 */
	void cacheBorderVision(int x, int y, CardinalDirection side, Visibility vision) {
		assert !side.isGrowing();
		int index = getBorderCacheIndex(x, y, side);
		borderVision[index] = vision;
	}

	public void saveCurrentCenterCoordinates(Cell character) {
		final int x = character.x();
		final int y = character.y();
		this.lastCenterCoordinates = new Cell() {

			@Override
			public int x() {
				return x;
			}

			@Override
			public int y() {
				return y;
			}
		};
	}
}
