package org.tendiwa.core.vision;

public class CellVisionCache {
public static final byte VISION_CACHE_WIDTH = (byte) (Seer.VISION_RANGE * 2 + 1);
byte[][] visionCache = new byte[VISION_CACHE_WIDTH][VISION_CACHE_WIDTH];

public byte[][] getContent() {
	return visionCache;
}
}
