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
		assertEquals(new Point2D(2, 2), line1.intersection(line2));
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

	@Test
	public void leftOfRay() {
		Segment2D segment = Segment2D.create(20, 20, 70, 60);
		Point2D[] leftPoints = new Point2D[]{
			new Point2D(40, 20),
			new Point2D(10, 0),
			new Point2D(140, 60)
		};
		Point2D[] nonLeftPoints = new Point2D[]{
			segment.start,
			segment.end,
			new Point2D(20, 30),
			new Point2D(0, 10),
			new Point2D(100, 200)
		};
		for (Point2D leftPoint : leftPoints) {
			assertTrue(segment.isLeftOfRay(leftPoint));
		}
		for (Point2D nonLeftPoint : nonLeftPoints) {
			assertFalse(segment.isLeftOfRay(nonLeftPoint));
		}
	}
}
