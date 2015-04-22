package org.tendiwa.demos;

import com.google.inject.Inject;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePoint2D;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.geometry.LineCircleIntersection;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.List;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public class LineCircleIntersectionDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(LineCircleIntersectionDemo.class);
	}

	@Override
	public void run() {
		Point2D a = point2D(100, 100);
		Point2D b = point2D(120, 180);
		Point2D c = point2D(130, 150);
		int radius = 70;
		List<Point2D> points = LineCircleIntersection.findIntersections(
			a,
			b,
			c,
			radius
		);
		canvas.draw(
			new DrawablePoint2D.Circle(
				c,
				Color.cyan,
				radius * 2
			)
		);
		canvas.draw(
			new DrawableSegment2D(
				segment2D(a, b),
				Color.red
			)
		);

		canvas.drawAll(
			points,
			p -> new DrawablePoint2D.Circle(p, Color.blue, 3)
		);
	}
}
