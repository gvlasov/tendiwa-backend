package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.Vector2D.vector;

public class Point2DTest {
	@Test
	public void testDistanceToLine() {
		Point2D point = new Point2D(20, 20);
		assertEquals(
			point.distanceToLine(Segment2D.create(20, 20, 30, 30)),
			0,
			1e-10
		);
		assertEquals(point.distanceToLine(
				Segment2D.create(40, 20, 40, 100)),
			20,
			1e-10
		);
		assertEquals(
			point.distanceToLine(Segment2D.create(40, 20, 20, 40)),
			20 / Math.sqrt(2),
			1e-10
		);
		assertEquals(
			point.distanceToLine(Segment2D.create(0, 20, 20, 0)),
			20 / Math.sqrt(2),
			1e-10
		);

	}

	@Test
	public void testDotProduct() {
		assertEquals(
			vector(0, 10).dotProduct(vector(5, 5)),
			5 * vector(0, 10).magnitude(),
			1e-10
		);
	}
}
