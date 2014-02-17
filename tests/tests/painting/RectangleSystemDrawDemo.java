package tests.painting;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.RecursivelySplitRectangleSystemFactory;
import org.tendiwa.drawing.DrawingRectangleSystem;
import org.tendiwa.drawing.TestCanvas;

import java.awt.*;

@RunWith(JukitoRunner.class)
public class RectangleSystemDrawDemo {
@Inject
TestCanvas canvas;

@Test
void draw() {
	long start = System.currentTimeMillis();
	RectangleSystem rs = RecursivelySplitRectangleSystemFactory.create(5, 5, 1260, 800, 1, 30);
//	RectangleSystem rs = builder(0)
//		.place(rectangle(10, 12), atPoint(5, 7))
//		.place(rectangle(14, 17), near(FIRST_RECTANGLE).fromSide(E).align(N))
//		.place(
//			rectangle(10, 5).repeat(10).placingNextAt(near(LAST_RECTANGLE).fromSide(E).align(S).shift(3)),
//			near(LAST_RECTANGLE).fromSide(E).align(N)
//		)
//		.place(
//			rectangle(4,9).repeat(9).placingNextAt(near(LAST_RECTANGLE).fromSide(S).align(W).shift(2)),
//			near(LAST_RECTANGLE).fromSide(S).align(E)
//		)
//		.done();
	// RectangleSystem rs = new RectangleSystem(0);
	// rs.addRectangleArea(10, 20, 30, 40);
	// RectangleArea r = rs.rectangleList().iterator().next();
	// rs.splitRectangle(r, Orientation.VERTICAL, 10, false);
	System.out.println(System.currentTimeMillis() - start);
	start = System.currentTimeMillis();
	canvas.draw(rs, DrawingRectangleSystem.graphAndRectangles(
		Color.RED,
		Color.BLACK,
		Color.DARK_GRAY,
		Color.GRAY,
		Color.LIGHT_GRAY)
	);
	System.out.println(System.currentTimeMillis() - start);
}
}
