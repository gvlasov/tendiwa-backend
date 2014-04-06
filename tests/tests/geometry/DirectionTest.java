package tests.geometry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.tendiwa.core.Directions.ALL_DIRECTIONS;
import static org.tendiwa.core.Directions.E;
import static org.tendiwa.core.Directions.N;
import static org.tendiwa.core.Directions.NE;
import static org.tendiwa.core.Directions.NW;
import static org.tendiwa.core.Directions.S;
import static org.tendiwa.core.Directions.SE;
import static org.tendiwa.core.Directions.SW;
import static org.tendiwa.core.Directions.W;

import org.junit.Test;

import org.tendiwa.core.Direction;

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
