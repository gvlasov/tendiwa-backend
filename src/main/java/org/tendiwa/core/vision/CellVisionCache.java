package org.tendiwa.core.vision;

public class CellVisionCache {
	public static final byte VISION_CACHE_WIDTH = (byte) (Seer.VISION_RANGE * 2 + 1);
	Visibility[][] visionCache = new Visibility[VISION_CACHE_WIDTH][VISION_CACHE_WIDTH];

	public Visibility[][] getContent() {
		return visionCache;
	}
}
