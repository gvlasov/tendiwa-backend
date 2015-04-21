package org.tendiwa.geometry;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.*;

public class OrthoCellSegment2DTest {
	@Test
	public void intersection() {
		Segment2D line1 = segment2D(0, 0, 4, 4);
		Segment2D line2 = segment2D(0, 4, 4, 0);
		assertEquals(
			point2D(2, 2),
			new BasicSegment2DIntersection(line1, line2)
				.point()
				.get()
		);
	}

	@Test
	public void parallelLinesDontIntersect() {
		Segment2D line1 = segment2D(0, 0, 4, 0);
		Segment2D line2 = segment2D(0, 1, 4, 1);
		assertFalse(
			new BasicSegment2DIntersection(line1, line2)
				.intersect()
		);
		assertFalse(
			new BasicSegment2DIntersection(line1, line2)
				.point()
				.isPresent()
		);
	}

	@Test
	public void noIntersectionOfShortSegmentsOnIntersectingLines() {
		Segment2D line1 = segment2D(151, 80, 150, 50);
		Segment2D line2 = segment2D(361, 208, 357, 179);
		StraightLineIntersection intersection = line1.intersectionWith(line2);
		assertFalse(intersection.intersect());
		assertFalse(intersection.point().isPresent());
	}

	@Test
	public void noIntersectionOnLineEnds() {

		Segment2D line1 = segment2D(0, 0, 4, 0);
		Segment2D line2 = segment2D(4, 0, 8, 0);
		assertFalse(line1.intersects(line2));
	}

	@Test
	public void parallel() {
		assertTrue(
			segment2D(0, 0, 0, 40).isParallel(segment2D(40, 0, 40, 40))
		);
		assertTrue(
			segment2D(0, 0, 30, 30).isParallel(segment2D(0, 10, 100, 110))
		);
		assertTrue(
			segment2D(0, 0, 0, 40).isParallel(segment2D(40, 40, 40, 0))
		);
	}

	@Test
	public void leftOfRay() {
		Segment2D segment = segment2D(20, 20, 70, 60);
		Stream.of(
			point2D(40, 20),
			point2D(10, 0),
			point2D(140, 60)
		).forEach(
			p-> assertTrue(segment.isLeftOfRay(p))
		);
	}

	@Test
	public void rightOfRayOrOnSegment() {
		Segment2D segment = segment2D(20, 20, 70, 60);
		Stream.of(
			segment.start(),
			segment.end(),
			point2D(20, 30),
			point2D(0, 10),
			point2D(100, 200)
		).forEach(
			p -> assertFalse(segment.isLeftOfRay(p))
		);
	}
}
