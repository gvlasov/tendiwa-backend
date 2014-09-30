package org.tendiwa.settlements.buildings;

import org.junit.Test;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Segment2D;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.Segment2D.create;
import static org.tendiwa.settlements.buildings.FairLotFacadeAndStreetAssigner.getDirectionToSegment;

public class FairLotFacadeAndStreetAssignerTest {
	Rectangle rectangle = new Rectangle(50, 50, 50, 50);

	@Test
	public void verticalWestSegments() {
		assertSegmentIsFromSide(create(25, 50, 25, 100), CardinalDirection.W);
		assertSegmentIsFromSide(create(25, 51, 25, 100), CardinalDirection.W);
		assertSegmentIsFromSide(create(25, 50, 25, 101), CardinalDirection.W);
		assertSegmentIsFromSide(create(25, 51, 25, 101), CardinalDirection.W);
		assertSegmentIsFromSide(create(25, 49, 25, 99), CardinalDirection.W);
	}

	@Test
	public void verticalEastSegments() {
		assertSegmentIsFromSide(create(125, 50, 125, 100), CardinalDirection.E);
		assertSegmentIsFromSide(create(125, 51, 125, 100), CardinalDirection.E);
		assertSegmentIsFromSide(create(125, 50, 125, 101), CardinalDirection.E);
		assertSegmentIsFromSide(create(125, 51, 125, 101), CardinalDirection.E);
		assertSegmentIsFromSide(create(125, 49, 125, 99), CardinalDirection.E);
	}

	@Test
	public void horizontalNorthSegments() {
		assertSegmentIsFromSide(create(50, 25, 100, 25), CardinalDirection.N);
		assertSegmentIsFromSide(create(49, 25, 100, 25), CardinalDirection.N);
		assertSegmentIsFromSide(create(50, 25, 99, 25), CardinalDirection.N);
		assertSegmentIsFromSide(create(51, 25, 101, 25), CardinalDirection.N);
		assertSegmentIsFromSide(create(49, 25, 99, 25), CardinalDirection.N);
	}

	@Test
	public void horizontalSouthSegments() {
		assertSegmentIsFromSide(create(50, 125, 100, 125), CardinalDirection.S);
		assertSegmentIsFromSide(create(49, 125, 100, 125), CardinalDirection.S);
		assertSegmentIsFromSide(create(50, 125, 99, 125), CardinalDirection.S);
		assertSegmentIsFromSide(create(51, 125, 101, 125), CardinalDirection.S);
		assertSegmentIsFromSide(create(49, 125, 99, 125), CardinalDirection.S);
	}

	/**
	 * Segments that touch rectangle's area in front of sides with only a single endpoint.
	 */
	@Test
	public void areaBorderSegments() {
		assertSegmentIsFromSide(create(20, 40, 50, 45), CardinalDirection.N);
		assertSegmentIsFromSide(create(70, 20, 125, 50), CardinalDirection.E);
		assertSegmentIsFromSide(create(100, 150, 110, 150), CardinalDirection.S);
		assertSegmentIsFromSide(create(20, 40, 25, 50), CardinalDirection.W);
	}

	/**
	 * Non axis-parallel segments that cross two borders of the area defined by the same side of a rectangle.
	 *
	 * @see org.tendiwa.settlements.buildings.FairLotFacadeAndStreetAssigner#pointIsFacingRectangleSide(org.tendiwa.geometry.Point2D,
	 * org.tendiwa.geometry.Rectangle)
	 */
	@Test
	public void areaTwoBorderCrossingSegments() {
		assertSegmentIsFromSide(create(30, 30, 120, 45), CardinalDirection.N);
		assertSegmentIsFromSide(create(101, 51, 500, 101), CardinalDirection.E);
		assertSegmentIsFromSide(create(60, 110, 120, 140), CardinalDirection.S);
		assertSegmentIsFromSide(create(30, 30, 45, 120), CardinalDirection.W);
	}

	/**
	 * Segments that have none of their points in areas in front of rectangle's sides.
	 *
	 * @see org.tendiwa.settlements.buildings.FairLotFacadeAndStreetAssigner#pointIsFacingRectangleSide(org.tendiwa.geometry.Point2D,
	 * org.tendiwa.geometry.Rectangle)
	 */
	@Test
	public void segmentsFullyNotInFrontOfRectangle() {
		// Segments to the north-east from rectangle
		assertSegmentIsFromSide(create(120, 30, 150, 35), CardinalDirection.N);
		assertSegmentIsFromSide(create(120, 20, 130, 45), CardinalDirection.E);

		// Fuck those test cases. I'm tired : (
	}

	private void assertSegmentIsFromSide(Segment2D segment, CardinalDirection side) {
		assertEquals(side, getDirectionToSegment(segment, rectangle));
	}
}
