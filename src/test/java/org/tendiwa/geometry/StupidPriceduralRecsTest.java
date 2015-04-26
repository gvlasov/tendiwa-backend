package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public class StupidPriceduralRecsTest {

	@Test
	public void testRectangleIntersectsSegment() {
		assertTrue(
			rectangle(10, 10, 10, 10).intersects(segment2D(5, 5, 15, 15))
		);
	}

	@Test
	public void testRectangleDoesntIntersectSegment() {
		assertFalse(
			rectangle(10, 10, 10, 10).intersects(segment2D(50, 50, 21, 15))
		);
		assertFalse(
			rectangle(40, 40, 40, 40).intersects(segment2D(20, 20, 20, 100))
		);
	}
}