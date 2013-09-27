package tests.painting;

import tendiwa.drawing.DrawingRectangleSystem;
import tendiwa.drawing.TestCanvas;
import tendiwa.drawing.TestCanvasBuilder;
import tendiwa.geometry.RandomRectangleSystem;
import tendiwa.geometry.RectangleSystem;

import java.awt.*;

import static tendiwa.geometry.DSL.*;

public class RectangleSystemDrawDemo {
public static TestCanvas canvas = new TestCanvasBuilder()
	.setSize(1280, 1024)
	.setDefaultDrawingAlgorithmForClass(
		RectangleSystem.class,
		DrawingRectangleSystem.withColors(Color.BLACK, Color.DARK_GRAY, Color.LIGHT_GRAY)
	)
	.build();
public static void main(String[] args) {
	long start = System.currentTimeMillis();
//	RectangleSystem rs = new RandomRectangleSystem(0, 0, 1280, 1024, 3, 0);
	RectangleSystem rs = builder(0)
		.place(rectangle(10, 12), atPoint(5, 7))
		.place(rectangle(14, 17), near(FIRST_RECTANGLE).fromSide(E).align(N))
		.done();
	// RectangleSystem rs = new RectangleSystem(0);
	// rs.addRectangleArea(10, 20, 30, 40);
	// RectangleArea r = rs.rectangleList().iterator().next();
	// rs.splitRectangle(r, Orientation.VERTICAL, 10, false);
	System.out.println(System.currentTimeMillis()-start);
	start = System.currentTimeMillis();
	canvas.draw(rs, DrawingRectangleSystem.graphAndRectangles(
		Color.RED,
		Color.BLACK,
		Color.DARK_GRAY,
		Color.GRAY,
		Color.LIGHT_GRAY)
	);
	System.out.println(System.currentTimeMillis()-start);
}
}
