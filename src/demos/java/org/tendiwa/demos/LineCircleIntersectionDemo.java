package org.tendiwa.demos;

import com.google.inject.Inject;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.LineCircleIntersection;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.List;

public class LineCircleIntersectionDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(LineCircleIntersectionDemo.class);
	}

	@Override
	public void run() {
		Point2D a = new Point2D(100, 100);
		Point2D b = new Point2D(120, 180);
		Point2D c = new Point2D(130, 150);
		int radius = 70;
		List<Point2D> points = LineCircleIntersection.findIntersections(
			a,
			b,
			c,
			radius
		);
		canvas.draw(c, DrawingPoint2D.withColorAndSize(Color.cyan, radius*2 ));
		canvas.draw(new Segment2D(a, b), DrawingSegment2D.withColor(Color.red));
		canvas.drawAll(points, DrawingPoint2D.withColorAndSize(Color.blue, 3));
	}
}
