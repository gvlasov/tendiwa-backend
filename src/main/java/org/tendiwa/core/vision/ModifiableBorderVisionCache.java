package org.tendiwa.core.vision;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.meta.CellPosition;

import java.util.Arrays;

public class ModifiableBorderVisionCache extends BorderVisionCache {
	private CellPosition lastCenterCoordinates;

	ModifiableBorderVisionCache(CellPosition character) {
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

	public void saveCurrentCenterCoordinates(CellPosition character) {
		final int x = character.getX();
		final int y = character.getY();
		this.lastCenterCoordinates = new CellPosition() {

			@Override
			public int getX() {
				return x;
			}

			@Override
			public int getY() {
				return y;
			}
		};
	}
}
