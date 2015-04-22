package org.tendiwa.demos.drawing;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.MagnifierCanvas;
import org.tendiwa.drawing.extensions.DrawablePoint2D;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.BasicRectangle2D;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;

final class Rectangle2D implements Runnable {
	public static void main(String[] args) {
		Demos.run(Rectangle2D.class);
	}

	@Override
	public void run() {
		DrawableInto canvas = new MagnifierCanvas(15, 6, 6, 150, 150);
		BasicRectangle2D box =
			new BasicRectangle2D(0, 0, 1, 1);
		BasicRectangle2D box2 =
			new BasicRectangle2D(1, 1, 1, 1);
		canvas.drawCell(0, 0, Color.black);
		canvas.drawRectangle2D(box, Color.red);
		canvas.drawRectangle2D(box2, Color.green);
		canvas.draw(
			new DrawablePoint2D.Billboard(
				point2D(6, 6),
				"Hello",
				Color.green,
				Color.blue
			)
		);


	}
}
