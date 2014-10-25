package org.tendiwa.drawing.extensions;

import org.tendiwa.core.Chunk;
import org.tendiwa.drawing.DrawingAlgorithm;

import java.awt.Color;


public class DrawingTerrain {

	public static DrawingAlgorithm<Chunk> defaultAlgorithm() {
		return (tb, canvas) -> {
			for (int x = 0, maxX = Chunk.SIZE; x < maxX; x++) {
				for (int y = 0, maxY = Chunk.SIZE; y < maxY; y++) {
					int rasterX = tb.x + x;
					int rasterY = tb.y + y;
					if (tb.hasCharacter(x, y)) {
						canvas.drawCell(rasterX, rasterY, Color.YELLOW);
					} else if (tb.hasObject(x, y)) {
						canvas.drawCell(rasterX, rasterY, Color.GRAY);
					} else {
						canvas.drawCell(rasterX, rasterY, Color.GREEN);
					}
				}
			}
		};
	}
}
