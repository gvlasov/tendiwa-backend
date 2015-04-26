package tests;

import org.junit.Test;
import org.tendiwa.core.Directions;
import org.tendiwa.geometry.Rectangle;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.*;

public class RectangleTest {

	@Test
	public void integerCenterPoint() {
		assertEquals(
			point2D(2, 2),
			rectangle(0, 0, 4, 4).getCenterPoint()
		);
	}

	@Test
	public void fractionalCenterPoint() {
		assertEquals(
			point2D(1.5, 1.5),
			rectangle(0, 0, 3, 3).getCenterPoint()
		);
	}

	@Test
	public void testGrowFromPoint() {
		assertEquals(
			rectangle(0, 0, 10, 10),
			cell(0, 0).growRectangle(Directions.SE, 10, 10)
		);
		assertEquals(
			rectangle(11, 20, 10, 10),
			cell(20, 20).growRectangle(Directions.SW, 10, 10)
		);
		assertEquals(
			rectangle(11, 11, 10, 10),
			cell(20, 20).growRectangle(Directions.NW, 10, 10)
		);
		assertEquals(
			rectangle(20, 11, 10, 10),
			cell(20, 20).growRectangle(Directions.NE, 10, 10)
		);
	}

	/**
	 * If rectangle 2 is completely inside rectangle 1, then their intersection is the whole rectangle 2.
	 */
	@Test
	public void intersectionBetween2RectanglesWhenOneIsInsideAnother() {
		Rectangle r1 = rectangle(-10, -20, 50, 90);
		Rectangle r2 = rectangle(10, 20, 30, 40);
		Rectangle intersection = r1.intersection(r2).get();
		assertEquals(r2, intersection);
	}

	/**
	 * Intersection of a rectangle with itself is this rectangle itself.
	 */
	@Test
	public void intersectionWithItself() {
		Rectangle r = rectangle(10, 10, 10, 10);
		Rectangle intersection = r.intersection(r).get();
		assertEquals(intersection, r);
	}
}
