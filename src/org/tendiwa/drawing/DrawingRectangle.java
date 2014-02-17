package org.tendiwa.drawing;

import com.google.common.collect.Iterables;
import org.tendiwa.geometry.EnhancedRectangle;
import org.tendiwa.geometry.Placeable;

import java.awt.*;
import java.util.Iterator;

public class DrawingRectangle {
public static DrawingAlgorithm<Placeable> withColor(final Color color) {
	return new DrawingAlgorithm<Placeable>() {
		@Override
		public void draw(Placeable shape) {
			for (EnhancedRectangle r : shape.getRectangles()) {
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
			for (EnhancedRectangle r : shape.getRectangles()) {
				drawRectangle(r, iter.next());
			}
		}
	};
}

public static DrawingAlgorithm<Placeable> chequerwise(final Color color1, final Color color2) {
	return new DrawingAlgorithm<Placeable>() {
		@Override
		public void draw(Placeable shape) {
			for (EnhancedRectangle r : shape.getRectangles()) {
				for (int i = r.getX(); i < r.getX() + r.getWidth() - 1; i++) {
					for (int j = r.getY(); j < r.getY() + r.getWidth() - 1; j++) {
						drawPoint(i, j, (i + j) % 2 == 1 ? color1 : color2);
					}
				}
			}

		}
	};
}
}
