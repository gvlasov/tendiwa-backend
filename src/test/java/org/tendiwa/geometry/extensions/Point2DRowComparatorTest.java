package org.tendiwa.geometry.extensions;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class Point2DRowComparatorTest {
	@Test
	public void testCompare() throws Exception {
		assertTrue(
			new Point2DRowComparator()
				.compare(
					point2D(1, 1), point2D(1, 2)
				) < 0
		);
		assertTrue(
			new Point2DRowComparator()
				.compare(
					point2D(1, 100.5), point2D(1, 100.5)
				) == 0
		);
		assertTrue(
			new Point2DRowComparator()
				.compare(
					point2D(5, 8), point2D(8, 5)
				) > 0
		);
	}
}
