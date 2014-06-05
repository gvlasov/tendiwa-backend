package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public class Segment2DTest {
	@Test
	public void intersection() {
		Segment2D line1 = new Segment2D(
			new Point2D(0, 0),
			new Point2D(4, 4)
		);
		Segment2D line2 = new Segment2D(
			new Point2D(0, 4),
			new Point2D(4, 0)
		);
		assertEquals(line1.intersection(line2), new Point2D(2, 2));
	}

	@Test
	public void noIntersectionOfParallel() {
		Segment2D line1 = new Segment2D(
			new Point2D(0, 0),
			new Point2D(4, 0)
		);
		Segment2D line2 = new Segment2D(
			new Point2D(0, 1),
			new Point2D(4, 1)
		);
		assertTrue(!line1.intersects(line2));
		assertNull(line1.intersection(line2));
	}

	@Test
	public void noIntersectionOfShortSegmentsOnIntersectingLines() {
		Segment2D line1 = new Segment2D(
			new Point2D(151, 80),
			new Point2D(150, 50)
		);
		Segment2D line2 = new Segment2D(
			new Point2D(361, 208),
			new Point2D(357, 179)
		);
		Point2D intersection = line1.intersection(line2);
		assertFalse(line1.intersects(line2));
		assertNull(intersection);
	}

	@Test
	public void noIntersectionOnLineEnds() {

		Segment2D line1 = new Segment2D(
			new Point2D(0, 0),
			new Point2D(4, 0)
		);
		Segment2D line2 = new Segment2D(
			new Point2D(4, 0),
			new Point2D(8, 0)
		);
		assertFalse(line1.intersects(line2));
	}

	@Test
	public void parallel() {
		boolean parallel = Segment2D.create(0, 0, 0, 40).isParallel(Segment2D.create(40, 0, 40, 40));
		assertTrue(parallel);
		parallel = Segment2D.create(0, 0, 30, 30).isParallel(Segment2D.create(0, 10, 100, 110));
		assertTrue(parallel);
		parallel = Segment2D.create(0, 0, 0, 40).isParallel(Segment2D.create(40, 40, 40, 0));
		assertTrue(parallel);
	}
}
