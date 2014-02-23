package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.tendiwa.core.Directions;
import org.tendiwa.geometry.Recs;

public class RectangleSystemTest {

	
	@Test
	public void testGrowRectangle() {
		assertEquals(
			Recs.growFromPoint(0, 0, Directions.NE, 4, 7),
				new java.awt.Rectangle(0, -7, 4, 7)
				);
		assertEquals(
			Recs.growFromPoint(0, 0, Directions.NW, 2, 1),
				new java.awt.Rectangle(-2, -1, 2, 1)
				);
		assertEquals(
			Recs.growFromPoint(0, 0, Directions.SW, 2, 3),
				new java.awt.Rectangle(-2, 0, 2, 3)
				);
		assertEquals(
			Recs.growFromPoint(0, 0, Directions.SE, 9, 3),
				new java.awt.Rectangle(0, 0, 9, 3)
				);
	}
}
