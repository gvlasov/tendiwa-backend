package tendiwa.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static tendiwa.geometry.Directions.E;
import static tendiwa.geometry.Directions.W;

import java.awt.Color;
import java.util.Set;

import org.junit.Test;

import tendiwa.drawing.DrawingRectangleSidePiece;
import tendiwa.drawing.TestCanvas;
import tendiwa.drawing.TestCanvasBuilder;

import com.google.common.collect.Sets;

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
		// TODO: This is a test for another class.
		EnhancedRectangle r1 = new RectangleArea(4, 5, 5, 5);
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
