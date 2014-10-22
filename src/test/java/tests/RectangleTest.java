package tests;

import org.junit.Test;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.*;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.DSL.*;

public class RectangleTest {

	/**
	 * Creates a minimal rectangle that contains specified cells (i.e., rectangular bound of thos cells).
	 */
	@Test
	public void buildLeastRectangularBoundForCells() {
		ArrayList<Cell> cells = new ArrayList<>();
		cells.add(new Cell(1, 12));
		cells.add(new Cell(12, 14));
		cells.add(new Cell(23, 29));
		cells.add(new Cell(23, 13));
		cells.add(new Cell(22, 0));
		Rectangle r = Recs.boundsOfCells(cells);
		assertEquals(r, new Rectangle(1, 0, 23, 30));
	}

	@Test
	public void centerPoint() {
		assertEquals(
			new Point2D(2, 2),
			new Rectangle(0, 0, 4, 4).getCenterPoint()
		);
		assertEquals(
			new Point2D(1.5, 1.5),
			new Rectangle(0, 0, 3, 3).getCenterPoint()
		);
	}

	@Test
	public void testGrowFromPoint() {
		assertEquals(
			new Rectangle(0, 0, 10, 10),
			Recs.growFromCell(0, 0, Directions.SE, 10, 10)
		);
		assertEquals(
			new Rectangle(11, 20, 10, 10),
			Recs.growFromCell(20, 20, Directions.SW, 10, 10)
		);
		assertEquals(
			new Rectangle(11, 11, 10, 10),
			Recs.growFromCell(20, 20, Directions.NW, 10, 10)
		);
		assertEquals(
			new Rectangle(20, 11, 10, 10),
			Recs.growFromCell(20, 20, Directions.NE, 10, 10)
		);

	}

	@Test
	public void projectRectangleOnAnotherRectangle() {
		RectangleSystemBuilder builder = builder(0)
			.place("one", rectangle(10, 20), atPoint(0, 0))
			.place("two", rectangle(20, 15), near(LAST_RECTANGLE).fromSide(E).align(S).shift(4));
		Rectangle rec1 = (Rectangle) builder.getByName("one");
		Rectangle rec2 = (Rectangle) builder.getByName("two");
		Segment projection = rec1.getProjectionSegment(rec2);
		assertEquals(
			new Segment(9, 9, 11, Orientation.VERTICAL),
			projection
		);
	}

	/**
	 * If rectangle 2 is completely inside rectangle 1, then their intersection is the whole rectangle 2.
	 */
	@Test
	public void intersectionBetween2RectanglesWhenOneIsInsideAnother() {
		Rectangle r1 = new Rectangle(-10, -20, 50, 90);
		Rectangle r2 = new Rectangle(10, 20, 30, 40);
		Rectangle intersection = r1.intersectionWith(r2).get();
		assertEquals(r2, intersection);
	}

	/**
	 * Intersection of a rectangle with itself is this rectangle itself.
	 */
	@Test
	public void intersectionWithItself() {
		Rectangle r = new Rectangle(10, 10, 10, 10);
		Rectangle intersection = r.intersectionWith(r).get();
		assertEquals(intersection, r);
	}

	/**
	 * Creates a rectangle using method {@link Recs#growFromCell(int, int, org.tendiwa.core.OrdinalDirection, int,
	 * int)}.
	 */
	@Test
	public void growFromPoint() {
		assertEquals(
			new Rectangle(0, -6, 4, 7),
			Recs.growFromCell(0, 0, Directions.NE, 4, 7)
		);
		assertEquals(
			new Rectangle(-1, 0, 2, 1),
			Recs.growFromCell(0, 0, Directions.NW, 2, 1)
		);
		assertEquals(
			new Rectangle(-1, 0, 2, 3),
			Recs.growFromCell(0, 0, Directions.SW, 2, 3)
		);
		assertEquals(
			new Rectangle(0, 0, 9, 3),
			Recs.growFromCell(0, 0, Directions.SE, 9, 3)
		);
	}

}
