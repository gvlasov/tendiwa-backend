package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.List;

public final class DrawingPolyline {
	public static DrawingAlgorithm<List<Point2D>> withColor(Color color) {
		return (polygon, canvas) -> {
			int prelastIndex = polygon.size()-1;
			for (int i = 0; i < prelastIndex; i++) {
				canvas.drawRasterLine(
					polygon.get(i).toCell(),
					polygon.get(i + 1).toCell(),
					color
				);
			}
		};
	}
}
