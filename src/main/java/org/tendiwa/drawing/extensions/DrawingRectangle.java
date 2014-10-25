package org.tendiwa.drawing.extensions;

import com.google.common.collect.Iterables;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;
import java.util.Iterator;

public class DrawingRectangle {
	public static DrawingAlgorithm<Placeable> withColor(final Color color) {
		return (shape, canvas) -> {
			for (Rectangle r : shape.getRectangles()) {
				canvas.drawRectangle(r, color);
			}
		};
	}

	public static DrawingAlgorithm<Placeable> withColorAndBorder(final Color fillColor, final Color borderColor) {
		return (shape, canvas) -> {
			for (Rectangle r : shape.getRectangles()) {
				canvas.drawRectangle(r, fillColor);
				canvas.drawLine(r.x, r.y, r.x + r.width - 1, r.y, borderColor);
				canvas.drawLine(r.x, r.y, r.x, r.y + r.height - 1, borderColor);
				canvas.drawLine(r.x + r.width - 1, r.y, r.x + r.width - 1, r.y + r.height - 1, borderColor);
				canvas.drawLine(r.x, r.y + r.height - 1, r.x + r.width - 1, r.y + r.height - 1, borderColor);
			}
		};
	}

	public static DrawingAlgorithm<Placeable> withColorLoop(final Color... colors) {
		final Iterator<Color> iter = Iterables.cycle(colors).iterator();
		return (shape, canvas) -> {
			for (Rectangle r : shape.getRectangles()) {
				canvas.drawRectangle(r, iter.next());
			}
		};
	}

	public static DrawingAlgorithm<Placeable> chequerwise(final Color color1, final Color color2) {
		return (shape, canvas) -> {
			for (Rectangle r : shape.getRectangles()) {
				for (int i = r.getX(); i < r.getX() + r.getWidth() - 1; i++) {
					for (int j = r.getY(); j < r.getY() + r.getWidth() - 1; j++) {
						canvas.drawCell(i, j, (i + j) % 2 == 1 ? color1 : color2);
					}
				}
			}

		};
	}
}
