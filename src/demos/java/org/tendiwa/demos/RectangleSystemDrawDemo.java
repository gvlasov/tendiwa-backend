package org.tendiwa.demos;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingRectangleSystem;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.geometry.extensions.RecursivelySplitRectangleSystemFactory;

import java.awt.*;

public class RectangleSystemDrawDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(RectangleSystemDrawDemo.class);
	}


	@Override
	public void run() {
		long start = System.currentTimeMillis();
		RectangleSystem rs = RecursivelySplitRectangleSystemFactory.create(0, 0, 1280, 1024, 20, 1);
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
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
