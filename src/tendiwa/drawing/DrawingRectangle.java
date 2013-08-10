package tendiwa.drawing;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;

import com.google.common.collect.Iterables;

public class DrawingRectangle {
	public static DrawingAlgorithm<Rectangle> withColor(final Color color) {
		return new DrawingAlgorithm<Rectangle>() {
			@Override
			public void draw(Rectangle shape) {
				drawRectangle(shape, color);
			}
		};
	}
	public static DrawingAlgorithm<Rectangle> withColorLoop(final Color... colors) {
		return new DrawingAlgorithm<Rectangle>() {
			final Iterator<Color> iter = Iterables.cycle(colors).iterator();

			@Override
			public void draw(Rectangle shape) {
				drawRectangle(shape, iter.next());
			}
		};
	}
	public static DrawingAlgorithm<Rectangle> chequerwise(final Color color1, final Color color2) {
		return new DrawingAlgorithm<Rectangle>() {
			@Override
			public void draw(Rectangle shape) {
				for (int i = shape.x; i < shape.x + shape.width - 1; i++) {
					for (int j = shape.y; j < shape.y + shape.width - 1; j++) {
						drawPoint(i, j, (i + j) % 2 == 1 ? color1 : color2);
					}
				}

			}
		};
	}
}
