package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;

public class ParallelSegmentDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(ParallelSegmentDemo.class, new DrawingModule());
	}


	@Override
	public void run() {
		Segment2D segment = Segment2D.create(20, 30, 70, 105);
		canvas.draw(segment, DrawingSegment2D.withColorDirected(Color.red, 3));
		canvas.draw(segment.createParallelSegment(10, true), DrawingSegment2D.withColorDirected(Color.blue, 3));
		canvas.draw(segment.createParallelSegment(20, false), DrawingSegment2D.withColorDirected(Color.green, 3));
		System.out.println(segment.createParallelSegment(10, true));
	}
}
