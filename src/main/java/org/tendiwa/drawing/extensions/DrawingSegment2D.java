package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;
import java.awt.geom.Line2D;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public class DrawingSegment2D {
	public static DrawingAlgorithm<Segment2D> withColorDirected(final Color color, double arrowheadLength) {
		return (shape, canvas) -> {
		};
	}

}
