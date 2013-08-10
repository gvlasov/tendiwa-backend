package tendiwa.drawing;

import java.awt.Color;

import tendiwa.core.TerrainBasics;

public class DrawingTerrain {

	public static DrawingAlgorithm<TerrainBasics> defaultAlgorithm() {
		return new DrawingAlgorithm<TerrainBasics>() {
			public void draw(TerrainBasics tb) {
				for (int x = 0, maxX = tb.getWidth(); x < maxX; x++) {
					for (int y = 0, maxY = tb.getHeight(); y < maxY; y++) {
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
