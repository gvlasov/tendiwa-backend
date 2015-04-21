package org.tendiwa.core.vision;

import org.tendiwa.core.Border;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.meta.Cell;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 */
public class BorderVisionCache implements Iterable<BorderVisibility> {
	protected static final int WIDTH = ModifiableCellVisionCache.VISION_CACHE_WIDTH + 1;
	protected static final int BORDER_VISION_CACHE_SIZE = WIDTH * WIDTH * 2;
	protected Cell center;
	Visibility[] borderVision = new Visibility[BORDER_VISION_CACHE_SIZE];

	BorderVisionCache(Cell center) {
		this.center = center;
		invalidate();
	}

	void invalidate() {
		Arrays.fill(borderVision, Visibility.NOT_COMPUTED);
	}

	public Visibility get(Border border) {
		return borderVision[getBorderCacheIndex(border.x, border.y, border.side)];
	}

	protected int getBorderCacheIndex(int x, int y, CardinalDirection side) {
		return (side == Directions.W ? 1 : 0) + (y - center.y() + Seer.VISION_RANGE) * 2 + (x - center.x() + Seer.VISION_RANGE) * WIDTH * 2;
	}

	@Override
	public Iterator<BorderVisibility> iterator() {
		return new Iterator<BorderVisibility>() {
			/**
			 * 0:0 is the top-left corner of visibility rectangle
			 * @see org.tendiwa.core.vision.Seer#getActualVisionRecStartPoint()
			 */
			private int currentRelativeX = 0;
			private int currentRelativeY = -1;
			private CardinalDirection currentSide = Directions.W;
			private int index = -1;

			@Override
			public boolean hasNext() {
				return index < borderVision.length - 1;
			}

			@Override
			public BorderVisibility next() {
				if (currentSide == Directions.W) {
					currentSide = Directions.N;
					if (currentRelativeY < WIDTH - 1) {
						currentRelativeY++;
					} else {
						currentRelativeX++;
						currentRelativeY = 0;
					}
				} else {
					currentSide = Directions.W;
				}
				index++;
				int worldX = currentRelativeX + center.x() - Seer.VISION_RANGE;
				int worldY = currentRelativeY + center.y() - Seer.VISION_RANGE;
				assert getBorderCacheIndex(
					worldX,
					worldY,
					currentSide
				) == index : getBorderCacheIndex(
					worldX,
					worldY,
					currentSide
				) + " " + index + " - " + currentRelativeX + " " + currentRelativeY + " " + currentSide;
				assert borderVision[index] != null;
				return new BorderVisibility(worldX, worldY, currentSide, borderVision[index]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public boolean isVisible(Border border) {
		int borderCacheIndex = getBorderCacheIndex(border.x, border.y, border.side);
		if (!isBorderInsideVisionRectangle(borderCacheIndex)) {
			return false;
		}
		Visibility visibility = borderVision[borderCacheIndex];
		assert visibility != null;
		return visibility == Visibility.VISIBLE;
	}

	/**
	 * Checks if there is an item in array {@link BorderVisionCache#borderVision} for a Border with given index
	 *
	 * @param borderIndex
	 * 	Result of {@link BorderVisionCache#getBorderCacheIndex(int, int, org.tendiwa.core.CardinalDirection)}.
	 * @return true if border is inside visibility rectangle (and not on rectangle's border itself), false otherwise.
	 */
	private boolean isBorderInsideVisionRectangle(int borderIndex) {
		return borderIndex >= 0 && borderIndex < BORDER_VISION_CACHE_SIZE;
	}
}
