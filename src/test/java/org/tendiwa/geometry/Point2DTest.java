package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public class Point2DTest {
	@Test
	public void testDistanceToLine() {
		Point2D point = new Point2D(20, 20);
		assertEquals(point.distanceToLine(Segment2D.create(20, 20, 30, 30)), 0, 0.0000000001);
		assertEquals(point.distanceToLine(Segment2D.create(40, 20, 40, 100)), 20, 0.0000000001);
		assertEquals(point.distanceToLine(Segment2D.create(40, 20, 20, 40)), 20/Math.sqrt(2), 0.0000000001);
		assertEquals(point.distanceToLine(Segment2D.create(0, 20, 20, 0)), 20/Math.sqrt(2), 0.0000000001);

	}
}
