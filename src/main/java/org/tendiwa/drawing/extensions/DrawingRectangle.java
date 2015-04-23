package org.tendiwa.drawing.extensions;

import com.google.common.collect.Iterables;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;
import java.util.Iterator;

public class DrawingRectangle {

	public static DrawingAlgorithm<RecTree> withColorAndBorder(final Color fillColor, final Color borderColor) {
		return (shape, canvas) -> {
			for (Rectangle r : shape.getRectangles()) {
			}
		};
	}

	public static DrawingAlgorithm<RecTree> withColorLoop(final Color... colors) {
		final Iterator<Color> iter = Iterables.cycle(colors).iterator();
		return (shape, canvas) -> {
			for (Rectangle r : shape.getRectangles()) {
				canvas.drawRectangle(r, iter.next());
			}
		};
	}

	public static DrawingAlgorithm<RecTree> chequerwise(final Color color1, final Color color2) {
	}
}
