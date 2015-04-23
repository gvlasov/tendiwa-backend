package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.GeometryPrimitives;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.EnclosedBlock;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.*;

public class DrawingEnclosedBlock {
	public static DrawingAlgorithm<EnclosedBlock> withColor(Color color) {
		return (shape, canvas) -> {
			Point2D previous = null;
			Point2D first = null;
			DrawingAlgorithm<Segment2D> drawingAlgorithm = DrawingSegment2D.withColorThin(color);
			for (Point2D point : shape) {
				if (previous != null) {
					canvas.draw(
						segment2D(previous, point),
						drawingAlgorithm
					);
				} else {
					first = point;
				}
				previous = point;
			}
			assert previous != null;
			canvas.draw(
				segment2D(previous, first),
				drawingAlgorithm
			);
		};
	}
}
