package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.EnclosedBlock;

import java.awt.Color;

public class DrawingEnclosedBlock {
	public static DrawingAlgorithm<EnclosedBlock> withColor(Color color) {
		return (shape, canvas) -> {
			Point2D previous = null;
			Point2D first = null;
			for (Point2D point : shape) {
				if (previous != null) {
					canvas.drawLine(previous.x, previous.y, point.x, point.y, color);
				} else {
					first = point;
				}
				previous = point;
			}
			assert previous != null;
			canvas.drawLine(previous.x, previous.y, first.x, first.y, color);
		};
	}
}
