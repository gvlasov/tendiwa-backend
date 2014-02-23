package org.tendiwa.drawing;

import com.google.common.collect.Iterables;
import org.jgrapht.Graph;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;
import java.util.Iterator;

/**
 * A static utility class that provides methods for creating {@link DrawingAlgorithm}s for drawing {@link
 * org.tendiwa.geometry.RectangleSystem}s.
 *
 * @author suseika
 */
public class DrawingRectangleSystem {
/**
 * Draws each rectangle of a {@link org.tendiwa.geometry.RectangleSystem} cycling over all colors.
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

public static DrawingAlgorithm<RectangleSystem> graphAndRectangles(final Color graphColor, final Color... colors) {
	return new DrawingAlgorithm<RectangleSystem>() {
		final Iterator<Color> iter = Iterables.cycle(colors).iterator();

		@Override
		public void draw(RectangleSystem rs) {
			for (Rectangle r : rs) {
				drawRectangle(r, iter.next());
			}
			Graph<Rectangle, RectangleSystem.Neighborship> graph = rs.getGraph();
			for (RectangleSystem.Neighborship edge : graph.edgeSet()) {
				drawLine(graph.getEdgeSource(edge).getCenterPoint(), graph.getEdgeTarget(edge).getCenterPoint(), graphColor);
			}
			for (Rectangle r : rs) {
				for (CardinalDirection dir : CardinalDirection.values()) {
					for (Segment segment : rs.getOuterSegmentsOf(r, dir)) {
						canvas.draw(segment);
					}
				}
			}
		}

	};
}

public static DrawingAlgorithm<RectangleSystem> neighborsUnionsAndRectangles(final Color neighborshipColor, final Color unionColor, final Color... colors) {
	return new DrawingAlgorithm<RectangleSystem>() {
		final Iterator<Color> iter = Iterables.cycle(colors).iterator();

		@Override
		public void draw(RectangleSystem rs) {
			for (Rectangle r : rs) {
				drawRectangle(r, iter.next());
			}
			Graph<org.tendiwa.geometry.Rectangle, RectangleSystem.Neighborship> graph = rs.getGraph();
			for (RectangleSystem.Neighborship edge : graph.edgeSet()) {
				drawLine(
					graph.getEdgeSource(edge).getCenterPoint(),
					graph.getEdgeTarget(edge).getCenterPoint(),
					(edge.isNeighborship()) ? neighborshipColor : unionColor
				);
			}
//			for (Rectangle r : rs) {
//				for (CardinalDirection dir : CardinalDirection.values()) {
//					for (Segment segment : rs.getOuterSegmentsOf(r, dir)) {
//						canvas.drawWorld(segment);
//					}
//				}
//			}
		}

	};
}

}
