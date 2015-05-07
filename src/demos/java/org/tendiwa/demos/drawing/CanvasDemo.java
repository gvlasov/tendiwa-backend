package org.tendiwa.demos.drawing;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableSegment2D;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public final class CanvasDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(CanvasDemo.class);
	}

	@Override
	public void run() {
		Canvas canvas = new TestCanvas(1, rectangle(300, 300));
		canvas.draw(
			new DrawableSegment2D(
				segment2D(0, 0, 150, 150),
				Color.red
			)
		);
		canvas.draw(
			new DrawableSegment2D(
				segment2D(90, 170, 280, 30),
				Color.green
			)
		);
	}
}
