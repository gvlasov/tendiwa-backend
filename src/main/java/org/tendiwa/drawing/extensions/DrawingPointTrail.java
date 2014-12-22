package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.List;

public final class DrawingPointTrail {
	public static DrawingAlgorithm<List<Point2D>> withColorThin(Color color) {
		return (shape, canvas) -> {
			int prelastIndex = shape.size()-1;
			for (int i=0; i< prelastIndex; i++) {
				Point2D start = shape.get(i);
				Point2D end = shape.get(i + 1);
				canvas.drawLine(start.x, start.y, end.x, end.y, color);
			}
		};
	}
}
