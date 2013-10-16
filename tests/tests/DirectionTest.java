package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static tendiwa.core.Directions.ALL_DIRECTIONS;
import static tendiwa.core.Directions.E;
import static tendiwa.core.Directions.N;
import static tendiwa.core.Directions.NE;
import static tendiwa.core.Directions.NW;
import static tendiwa.core.Directions.S;
import static tendiwa.core.Directions.SE;
import static tendiwa.core.Directions.SW;
import static tendiwa.core.Directions.W;

import org.junit.Test;

import tendiwa.core.Direction;

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
		}
	}

}
