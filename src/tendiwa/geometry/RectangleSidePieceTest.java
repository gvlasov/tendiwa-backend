package tendiwa.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static tendiwa.geometry.Directions.CARDINAL_DIRECTIONS;
import static tendiwa.geometry.Directions.E;
import static tendiwa.geometry.Directions.W;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

import org.junit.Test;

public class RectangleSidePieceTest {
	@Test
	public void testStaticCoordinateInsideAndOutside() {
		RectangleArea r = new RectangleArea(5, 8, 5, 5);
		int staticCoordinateInside = r
			.getSideAsSidePiece(E)
			.getLine()
			.getStaticCoordFromSide(W);
		int staticCoordinateOutside = r
			.getSideAsSidePiece(E)
			.getLine()
			.getStaticCoordFromSide(E);
		assertEquals(staticCoordinateInside, staticCoordinateOutside - 1);
	}

	@Test
	public void touches() {
		EnhancedRectangle r1 = new RectangleArea(4, 5, 5, 5);
		EnhancedRectangle r2 = r1
			.getSideAsSidePiece(Directions.E)
			.createRectangle(5);
		assertTrue(r1.touches(r2.getSideAsSidePiece(Directions.W)));
	}
	@Test public void contains() {
		RectangleSidePiece bigPiece = new RectangleSidePiece(Directions.N, 5, 9, 17);
		RectangleSidePiece smallerPiece = new RectangleSidePiece(Directions.N, 12, 9, 10);
		RectangleSidePiece wrongPiece = new RectangleSidePiece(Directions.N, 4, 9, 3);
		assertTrue(bigPiece.contains(smallerPiece));
		assertFalse(bigPiece.contains(wrongPiece));
		assertTrue(wrongPiece.contains(wrongPiece));
	}
}
