package org.tendiwa.demos.drawing;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.MagnifierCanvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;

final class Rectangle2D implements Runnable {
	public static void main(String[] args) {
		Demos.run(Rectangle2D.class);
	}

	@Override
	public void run() {
		DrawableInto canvas = new MagnifierCanvas(15, 6, 6, 150, 150);
		org.tendiwa.geometry.Rectangle2D box =
			new org.tendiwa.geometry.Rectangle2D(0, 0, 1, 1);
		org.tendiwa.geometry.Rectangle2D box2 =
			new org.tendiwa.geometry.Rectangle2D(1, 1, 1, 1);
		canvas.drawCell(0, 0, Color.black);
		canvas.drawRectangle2D(box, Color.red);
		canvas.drawRectangle2D(box2, Color.green);
		canvas.draw(
			new Point2D(6, 6),
			DrawingPoint2D.withTextMarker(
				"Hello",
				Color.green,
				Color.blue
			)
		);


	}
}
