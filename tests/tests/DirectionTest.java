package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static tendiwa.geometry.Directions.ALL_DIRECTIONS;
import static tendiwa.geometry.Directions.E;
import static tendiwa.geometry.Directions.N;
import static tendiwa.geometry.Directions.NE;
import static tendiwa.geometry.Directions.NW;
import static tendiwa.geometry.Directions.S;
import static tendiwa.geometry.Directions.SE;
import static tendiwa.geometry.Directions.SW;
import static tendiwa.geometry.Directions.W;

import org.junit.Test;

import tendiwa.geometry.Direction;

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
