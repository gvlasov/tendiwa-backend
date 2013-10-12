package tendiwa.drawing;

import tendiwa.core.Cell;
import tendiwa.core.StaticData;
import tendiwa.core.World;

import java.awt.*;

public class DrawingWorld {

public static DrawingAlgorithm<World> defaultAlgorithm() {
	return new DrawingAlgorithm<World>() {
		@Override
		public void draw(World world) {
			Cell[][] cellContents = world.getCellContents();
			int width = cellContents.length;
			int height = cellContents[0].length;
			if (width > canvas.width || height > canvas.height) {
				throw new RuntimeException("Size of world ("+width+"x"+height+") is greater than size of canvas ("+width+"x"+height+")");
			}
			drawRectangle(new Rectangle(0, 0, width, height), Color.BLACK);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Cell cell = cellContents[x][y];
					if (cell.object() != StaticData.VOID) {
						drawPoint(x, y, Color.DARK_GRAY);
					} else if (cell.floor() != StaticData.VOID) {
						drawPoint(x, y, Color.GREEN);
					} else if (cell.character() != null) {
						drawPoint(x, y, Color.YELLOW);
					}
				}
			}
		}
	};
}
}
