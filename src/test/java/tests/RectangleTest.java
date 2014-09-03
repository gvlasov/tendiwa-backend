package tests;

import org.junit.Test;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.tendiwa.geometry.DSL.*;

public class RectangleTest {

	@Test
	public void testRectangleContainingPoints() {
		ArrayList<Cell> points = new ArrayList<>();
		points.add(new Cell(1, 12));
		points.add(new Cell(12, 14));
		points.add(new Cell(23, 29));
		points.add(new Cell(23, 13));
		points.add(new Cell(22, 0));
		Rectangle r = Recs.boundsOfCells(points);
		assertEquals(r, new Rectangle(1, 0, 23, 30));
	}

	@Test
	public void testCenterPoint() {
		assertEquals(new Rectangle(0, 0, 4, 4).getCenterPoint(), new Point2D(2, 2));
		assertEquals(new Rectangle(0, 0, 3, 3).getCenterPoint(), new Point2D(1.5, 1.5));
	}

	@Test
	public void testGrowFromPoint() {
		assertEquals(
			Recs.growFromPoint(0, 0, Directions.SE, 10, 10),
			new Rectangle(0, 0, 10, 10));
		assertEquals(
			Recs.growFromPoint(20, 20, Directions.SW, 10, 10),
			new Rectangle(11, 20, 10, 10));
		assertEquals(
			Recs.growFromPoint(20, 20, Directions.NW, 10, 10),
			new Rectangle(11, 11, 10, 10));
		assertEquals(
			Recs.growFromPoint(20, 20, Directions.NE, 10, 10),
			new Rectangle(20, 11, 10, 10));

	}

	@Test
	public void adjacencySegmentTest() {
		RectangleSystemBuilder builder = builder(0)
			.place("one", rectangle(10, 20), atPoint(0, 0))
			.place("two", rectangle(20, 15), near(LAST_RECTANGLE).fromSide(E).align(S).shift(4));
		Rectangle rec1 = (Rectangle) builder.getByName("one");
		Rectangle rec2 = (Rectangle) builder.getByName("two");
		assertEquals(rec1.getIntersectionSegment(rec2), new Segment(11, 9, 11, Orientation.VERTICAL));
	}

	@Test
	public void intersectionTest() {
		Rectangle intersection;
		intersection = new Rectangle(-10, -20, 50, 90)
			.intersectionWith(new Rectangle(10, 20, 30, 40))
			.get();
		assertEquals(intersection, new Rectangle(10, 20, 30, 40));
	}

	@Test
	public void intersectionWithItself() {
		Rectangle itself = new Rectangle(10, 10, 10, 10);
		Rectangle intersection = itself
			.intersectionWith(itself)
			.get();
		assertEquals(intersection, itself);
	}

}
