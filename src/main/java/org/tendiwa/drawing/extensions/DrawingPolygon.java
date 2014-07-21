package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.List;

public class DrawingPolygon {
	public static DrawingAlgorithm<List<Point2D>> withColor(Color color) {
		return (polygon, canvas) -> {
			int size = polygon.size();
			for (int i = 0; i < size; i++) {
				canvas.drawLine(
					polygon.get(i).toCell(),
					polygon.get(i + 1 == size ? 0 : i + 1).toCell(),
					color
				);
			}
		};
	}
}
