package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.line2D;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class Line2DTest {
	@Test
	public void testPerpendicularIntersection() {
		assertEquals(
			point2D(6, 4),
			line2D(4, 4, 8, 4).intersectionWith(line2D(6, 6, 6, 0)).get()
		);

	}

	@Test
	public void testEqualNoIntersection() {
		assertFalse(line2D(4, 4, 8, 4).intersectionWith(line2D(4, 4, 8, 4)).isPresent());
	}

	@Test
	public void testParallelNoIntersection() {
		assertNull(
			line2D(4, 4, 8, 4).intersectionWith(line2D(4, 3, 8, 3)).isPresent()
		);
	}
}
