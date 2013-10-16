package tests;

import junit.framework.TestCase;

import org.junit.Test;

import tendiwa.core.Directions;

public class SideTest extends TestCase {

	@Test
	public void testshiftToDirection() {
		System.out.println(Directions.shiftToDirection(1, 4));
		assertEquals(Directions.shiftToDirection(1, -4), Directions.N);
		assertEquals(Directions.shiftToDirection(-1, -4), Directions.N);
		assertEquals(Directions.shiftToDirection(-1, 4), Directions.S);
		assertEquals(Directions.shiftToDirection(1, 4), Directions.S);
		assertEquals(Directions.shiftToDirection(4, 1), Directions.E);
		assertEquals(Directions.shiftToDirection(4, -1), Directions.E);
		assertEquals(Directions.shiftToDirection(-4, 1), Directions.W);
		assertEquals(Directions.shiftToDirection(-4, -1), Directions.W);

		assertEquals(Directions.shiftToDirection(-4, 0), Directions.W);
		assertEquals(Directions.shiftToDirection(4, 0), Directions.E);
		assertEquals(Directions.shiftToDirection(0, 4), Directions.S);
		assertEquals(Directions.shiftToDirection(0, -4), Directions.N);

		assertEquals(Directions.shiftToDirection(-4, -4), Directions.NW);
		assertEquals(Directions.shiftToDirection(4, -4), Directions.NE);
		assertEquals(Directions.shiftToDirection(4, 4), Directions.SE);
		assertEquals(Directions.shiftToDirection(-4, 4), Directions.SW);

	}

}
