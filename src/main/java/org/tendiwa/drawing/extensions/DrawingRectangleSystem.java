package org.tendiwa.drawing.extensions;

import com.google.common.collect.Iterables;
import org.jgrapht.Graph;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;

import java.awt.*;
import java.util.Iterator;

/**
 * A static utility class that provides methods for creating {@link org.tendiwa.drawing.DrawingAlgorithm}s for drawing {@link
 * org.tendiwa.geometry.RectangleSystem}s.
 *
 * @author suseika
 */
public class DrawingRectangleSystem {
    /**
     * Draws each rectangle of a {@link org.tendiwa.geometry.RectangleSystem} cycling over all colors.
     *
     * @param colors
     *         Colors to draw rectangles.
     */
    public static DrawingAlgorithm<RectangleSystem> withColors(final Color... colors) {
        final Iterator<Color> iterator = Iterables.cycle(colors).iterator();
        return (rs, canvas) -> {
            for (Rectangle r : rs) {
                canvas.drawRectangle(r, iterator.next());
            }
        };
    }

    public static DrawingAlgorithm<RectangleSystem> graphAndRectangles(final Color graphColor, final Color... colors) {
        final Iterator<Color> iter = Iterables.cycle(colors).iterator();
        return (rs, canvas) -> {
            for (Rectangle r : rs) {
                canvas.drawRectangle(r, iter.next());
            }
            Graph<Rectangle, RectangleSystem.Neighborship> graph = rs.getGraph();
            for (RectangleSystem.Neighborship edge : graph.edgeSet()) {
                canvas.drawLine(
                        graph.getEdgeSource(edge).getCenterPoint(),
                        graph.getEdgeTarget(edge).getCenterPoint(),
                        graphColor
                );
            }
            DrawingAlgorithm<Segment> red = DrawingSegment.withColor(Color.RED);
            for (Rectangle r : rs) {
                for (CardinalDirection dir : CardinalDirection.values()) {
                    for (Segment segment : rs.getOuterSegmentsOf(r, dir)) {
                        canvas.draw(segment, red);
                    }
                }
            }
        };
    }

    public static DrawingAlgorithm<RectangleSystem> neighborsUnionsAndRectangles(final Color neighborshipColor, final Color unionColor, final Color... colors) {
        final Iterator<Color> iter = Iterables.cycle(colors).iterator();
        return (rs, canvas) -> {
            for (Rectangle r : rs) {
                canvas.drawRectangle(r, iter.next());
            }
            Graph<Rectangle, RectangleSystem.Neighborship> graph = rs.getGraph();
            for (RectangleSystem.Neighborship edge : graph.edgeSet()) {
                canvas.drawLine(
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
        };
    }

}
