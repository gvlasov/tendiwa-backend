package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.List;

public final class DrawingPolyline {
	public static DrawingAlgorithm<List<Point2D>> withColor(Color color) {
		return (polyline, canvas) -> {
			int prelastIndex = polyline.size()-1;
			for (int i = 0; i < prelastIndex; i++) {
				canvas.drawRasterLine(
					polyline.get(i).toCell(),
					polyline.get(i + 1).toCell(),
					color
				);
			}
		};
	}
}
