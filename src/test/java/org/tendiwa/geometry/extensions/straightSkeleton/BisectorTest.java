package org.tendiwa.geometry.extensions.straightSkeleton;

import org.junit.Test;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;

public class BisectorTest {

	@Test
	public void testAsSegment() throws Exception {
		TestCanvas canvas = new TestCanvas(2, 400, 400);
		TestCanvas.canvas = canvas;
		Segment2D previous = Segment2D.create(20, 20, 40, 40);
		Segment2D current = Segment2D.create(40, 40, 20, 60);
		Bisector bisector = new Bisector(previous, current, previous.end, false);
		Segment2D bisectorSegment = bisector.asSegment(20);
		Segment2D bisectorSegmentO = bisector.asSegment(40);
		canvas.draw(previous, DrawingSegment2D.withColorThin(Color.red));
		canvas.draw(current, DrawingSegment2D.withColorThin(Color.red));
		canvas.draw(bisectorSegment, DrawingSegment2D.withColorThin(Color.blue));
		canvas.draw(bisectorSegmentO, DrawingSegment2D.withColorThin(Color.green));
		Demos.sleepIndefinitely();
	}
}