package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public class Line2DTest {
	@Test
	public void testPerpendicularIntersection() {
		Point2D intersection = new Line2D(4, 4, 8, 4).intersectionWith(new Line2D(6, 6, 6, 0));
		assertEquals(new Point2D(6, 4), intersection);

	}

	@Test
	public void testEqualNoIntersection() {
		Point2D intersection = new Line2D(4, 4, 8, 4).intersectionWith(new Line2D(4, 4, 8, 4));
		assertNull(intersection);

	}

	@Test
	public void testParallelNoIntersection() {
		Point2D intersection = new Line2D(4, 4, 8, 4).intersectionWith(new Line2D(4, 3, 8, 3));
		assertNull(intersection);
	}
}
