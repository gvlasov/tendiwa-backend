package tests.geometry;

import org.junit.Test;
import org.tendiwa.core.Direction;

import static org.junit.Assert.*;
import static org.tendiwa.core.Directions.*;

public class DirectionTest {

	@Test
	public void perpendicular() {
		assertTrue(E.isPerpendicular(S));
		assertTrue(S.isPerpendicular(W));
		assertTrue(W.isPerpendicular(S));
		assertTrue(N.isPerpendicular(E));

		assertTrue(NE.isPerpendicular(SE));
		assertTrue(NE.isPerpendicular(NW));
		assertTrue(SE.isPerpendicular(SW));
		assertTrue(SW.isPerpendicular(SE));

		assertFalse(N.isPerpendicular(SE));
		assertFalse(S.isPerpendicular(SE));
		assertFalse(E.isPerpendicular(SW));

		assertFalse(SE.isPerpendicular(N));
		assertFalse(SW.isPerpendicular(E));
		assertFalse(NE.isPerpendicular(S));
		for (Direction dir : ALL_DIRECTIONS) {
			assertFalse(dir.isPerpendicular(dir));
			assertFalse(dir.isPerpendicular(dir.opposite()));
			assertFalse(dir.isPerpendicular(dir.clockwise()));
			assertFalse(dir.isPerpendicular(dir.counterClockwise()));
			assertFalse(dir.isPerpendicular(dir.opposite().clockwise()));
			assertFalse(dir.isPerpendicular(dir.opposite().counterClockwise()));
		}
	}

}
