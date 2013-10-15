package tendiwa.drawing;

import com.google.common.collect.Iterables;
import tendiwa.geometry.Placeable;

import java.awt.*;
import java.util.Iterator;

public class DrawingRectangle {
public static DrawingAlgorithm<Placeable> withColor(final Color color) {
	return new DrawingAlgorithm<Placeable>() {
		@Override
		public void draw(Placeable shape) {
			for (Rectangle r : shape.getRectangles()) {
				drawRectangle(r, color);
			}
		}
	};
}

public static DrawingAlgorithm<Placeable> withColorLoop(final Color... colors) {
	return new DrawingAlgorithm<Placeable>() {
		final Iterator<Color> iter = Iterables.cycle(colors).iterator();

		@Override
		public void draw(Placeable shape) {
			for (Rectangle r : shape.getRectangles()) {
				drawRectangle(r, iter.next());
			}
		}
	};
}

public static DrawingAlgorithm<Placeable> chequerwise(final Color color1, final Color color2) {
	return new DrawingAlgorithm<Placeable>() {
		@Override
		public void draw(Placeable shape) {
			for (Rectangle r : shape.getRectangles()) {
				for (int i = r.x; i < r.x + r.width - 1; i++) {
					for (int j = r.y; j < r.y + r.width - 1; j++) {
						drawPoint(i, j, (i + j) % 2 == 1 ? color1 : color2);
					}
				}
			}

		}
	};
}
}
