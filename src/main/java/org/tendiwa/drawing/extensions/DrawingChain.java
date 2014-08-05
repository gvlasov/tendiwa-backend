package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.List;

public class DrawingChain {
	public static DrawingAlgorithm<List<Point2D>> withColor(Color color) {
		return (chain, canvas) -> {
			Point2D previous = null;
			for (Point2D vertex : chain) {
				if (previous == null) {
					previous = vertex;
					continue;
				}
				canvas.drawLine(
					previous.x,
					previous.y,
					vertex.x,
					vertex.y,
					color
				);
				previous = vertex;
			}
		};
	}
}
