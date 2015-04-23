package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.ParallelSegment;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public class ParallelSegmentDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(ParallelSegmentDemo.class, new DrawingModule());
	}


	@Override
	public void run() {
		Segment2D segment = segment2D(20, 30, 70, 105);
		ParallelSegment parallel1 = new ParallelSegment(segment, 10, true);
		ParallelSegment parallel2 = new ParallelSegment(segment, 20, false);
		canvas.draw(
			new DrawableSegment2D.Arrow(
				segment,
				Color.red,
				3
			)
		);
		canvas.draw(
			new DrawableSegment2D.Arrow(
				parallel1,
				Color.black,
				3
			)
		);
		canvas.draw(
			new DrawableSegment2D.Arrow(
				parallel2,
				Color.green,
				3
			)
		);
	}
}
