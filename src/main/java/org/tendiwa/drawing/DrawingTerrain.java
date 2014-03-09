package org.tendiwa.drawing;

import org.tendiwa.core.Chunk;

import java.awt.Color;


public class DrawingTerrain {

	public static DrawingAlgorithm<Chunk> defaultAlgorithm() {
		return new DrawingAlgorithm<Chunk>() {
			public void draw(Chunk tb) {
				for (int x = 0, maxX = Chunk.SIZE; x < maxX; x++) {
					for (int y = 0, maxY = Chunk.SIZE; y < maxY; y++) {
						int rasterX = tb.x + x;
						int rasterY = tb.y + y;
						if (tb.hasCharacter(x, y)) {
							drawPoint(rasterX, rasterY, Color.YELLOW);
						} else if (tb.hasObject(x, y)) {
							drawPoint(rasterX, rasterY, Color.GRAY);
						} else {
							drawPoint(rasterX, rasterY, Color.GREEN);
						}
					}
				}
			}
		};
	}
}
