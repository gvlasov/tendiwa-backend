package org.tendiwa.settlements.buildings;

import org.junit.Test;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.Rectangle;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.Segment2D.create;
import static org.tendiwa.settlements.buildings.FairLotFacadeAndStreetAssigner.getDirectionToSegment;

public class FairLotFacadeAndStreetAssignerTest {
	Rectangle rectangle = new Rectangle(50, 50, 50, 50);

	@Test
	public void testVerticalWestSegments() {
		assertEquals(CardinalDirection.W, getDirectionToSegment(create(25, 50, 25, 100), rectangle));
		assertEquals(CardinalDirection.W, getDirectionToSegment(create(25, 51, 25, 100), rectangle));
		assertEquals(CardinalDirection.W, getDirectionToSegment(create(25, 50, 25, 101), rectangle));
		assertEquals(CardinalDirection.W, getDirectionToSegment(create(25, 51, 25, 101), rectangle));
		assertEquals(CardinalDirection.W, getDirectionToSegment(create(25, 49, 25, 99), rectangle));
	}

	@Test
	public void testVerticalEastSegments() {
		assertEquals(CardinalDirection.E, getDirectionToSegment(create(125, 50, 125, 100), rectangle));
		assertEquals(CardinalDirection.E, getDirectionToSegment(create(125, 51, 125, 100), rectangle));
		assertEquals(CardinalDirection.E, getDirectionToSegment(create(125, 50, 125, 101), rectangle));
		assertEquals(CardinalDirection.E, getDirectionToSegment(create(125, 51, 125, 101), rectangle));
		assertEquals(CardinalDirection.E, getDirectionToSegment(create(125, 49, 125, 99), rectangle));
	}

	@Test
	public void testHorizontalNorthSegments() {
		assertEquals(CardinalDirection.N, getDirectionToSegment(create(50, 25, 100, 25), rectangle));
		assertEquals(CardinalDirection.N, getDirectionToSegment(create(49, 25, 100, 25), rectangle));
		assertEquals(CardinalDirection.N, getDirectionToSegment(create(50, 25, 99, 25), rectangle));
		assertEquals(CardinalDirection.N, getDirectionToSegment(create(51, 25, 101, 25), rectangle));
		assertEquals(CardinalDirection.N, getDirectionToSegment(create(49, 25, 99, 25), rectangle));
	}

	@Test
	public void testHorizontalSouthSegments() {
		assertEquals(CardinalDirection.S, getDirectionToSegment(create(50, 125, 100, 125), rectangle));
		assertEquals(CardinalDirection.S, getDirectionToSegment(create(49, 125, 100, 125), rectangle));
		assertEquals(CardinalDirection.S, getDirectionToSegment(create(50, 125, 99, 125), rectangle));
		assertEquals(CardinalDirection.S, getDirectionToSegment(create(51, 125, 101, 125), rectangle));
		assertEquals(CardinalDirection.S, getDirectionToSegment(create(49, 125, 99, 125), rectangle));
	}

	@Test
	/**
	 * Segments that thouch rectangle's area in front of sides with only a single endpoint.
	 */
	public void testAreaBorderSegments() {
//		assertEquals(CardinalDirection.W, getDirectionToSegment(create(50, 40, )));
	}
}