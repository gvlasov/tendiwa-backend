package tests;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.tendiwa.core.Directions;
import org.tendiwa.core.EnhancedRectangle;
import org.tendiwa.core.RectangleSidePiece;

import java.util.Set;

import static org.junit.Assert.*;
import static org.tendiwa.core.Directions.E;
import static org.tendiwa.core.Directions.W;

public class RectangleSidePieceTest {
	@Test
	public void testStaticCoordinateInsideAndOutside() {
		EnhancedRectangle r = new EnhancedRectangle(5, 8, 5, 5);
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
		// TODO: This is a test for another class.
		EnhancedRectangle r1 = new EnhancedRectangle(4, 5, 5, 5);
		EnhancedRectangle r2 = r1
			.getSideAsSidePiece(Directions.E)
			.createRectangle(5);
		assertTrue(r1.touches(r2.getSideAsSidePiece(Directions.W)));
	}
	@Test
	public void contains() {
		RectangleSidePiece bigPiece = new RectangleSidePiece(
			Directions.N,
			5,
			9,
			17);
		RectangleSidePiece smallerPiece = new RectangleSidePiece(
			Directions.N,
			12,
			9,
			10);
		RectangleSidePiece wrongPiece = new RectangleSidePiece(
			Directions.N,
			4,
			9,
			3);
		assertTrue(bigPiece.contains(smallerPiece));
		assertFalse(bigPiece.contains(wrongPiece));
		assertTrue(wrongPiece.contains(wrongPiece));
	}
	@Test
	public void splitWithPieces() {
		RectangleSidePiece piece = new RectangleSidePiece(
			Directions.N,
			4,
			6,
			16);
		Set<RectangleSidePiece> pieces = Sets.newHashSet(
			new RectangleSidePiece(Directions.S, 5, 5, 8),
			new RectangleSidePiece(Directions.S, 13, 5, 1));
		Set<RectangleSidePiece> splitPieces = Sets.newHashSet(piece
			.splitWithPieces(pieces));
		assertTrue(new RectangleSidePiece(Directions.N, 4, 6, 1)
			.equals(new RectangleSidePiece(Directions.N, 4, 6, 1)));
		assertFalse(new RectangleSidePiece(Directions.N, 4, 6, 2)
			.equals(new RectangleSidePiece(Directions.N, 4, 6, 1)));
		assertTrue(splitPieces.contains(new RectangleSidePiece(
			Directions.N,
			4,
			6,
			1)));
		assertTrue(splitPieces.contains(new RectangleSidePiece(
			Directions.N,
			14,
			6,
			6)));
		assertTrue(splitPieces.size() == 2);
	}
}
