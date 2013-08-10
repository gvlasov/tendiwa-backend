package tendiwa.drawing;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;

import tendiwa.geometry.RectangleSystem;

import com.google.common.collect.Iterables;

/**
 * A static utility class that provides methods for creating
 * {@link DrawingAlgorithm}s for drawing {@link RectangleSystem}s.
 * 
 * @author suseika
 * 
 */
public class DrawingRectangleSystem {
	/**
	 * Draws each rectangle of a {@link RectangleSystem} cycling over all
	 * colors.
	 * 
	 * @param colors
	 * @return drawing algorithm
	 */
	public static DrawingAlgorithm<RectangleSystem> withColors(final Color... colors) {
		return new DrawingAlgorithm<RectangleSystem>() {
			final Iterator<Color> iter = Iterables.cycle(colors).iterator();

			@Override
			public void draw(RectangleSystem rs) {
				for (Rectangle r : rs) {
					drawRectangle(r, iter.next());
				}
			}
		};
	}
}
