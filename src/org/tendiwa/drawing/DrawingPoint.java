package org.tendiwa.drawing;

import java.awt.Color;
import java.awt.Point;

public final class DrawingPoint {
	private DrawingPoint() {

	}
	public static DrawingAlgorithm<Point> withColor(final Color color) {
		return new DrawingAlgorithm<Point>() {
			@Override
			public void draw(Point point) {
				drawPoint(point.x, point.y, color);
			}
		};
	}
}
