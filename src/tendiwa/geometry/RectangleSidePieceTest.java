package tendiwa.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static tendiwa.geometry.Directions.CARDINAL_DIRECTIONS;
import static tendiwa.geometry.Directions.E;
import static tendiwa.geometry.Directions.W;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

import org.junit.Test;

public class RectangleSidePieceTest {
	private final Comparator<RectangleSidePiece> PIECES_COMPARATOR = new Comparator<RectangleSidePiece>() {
		@Override
		public int compare(RectangleSidePiece piece1, RectangleSidePiece piece2) {
			// TODO: Remove after debugging
			if (piece1.direction != piece2.direction) {
				throw new Error("Comparing pieces with different directions");
			}
			switch (piece1.direction) {
				case N:
					return -(piece1.segment.y - piece2.segment.y);
				case E:
					return piece1.segment.x - piece2.segment.x;
				case S:
					return piece1.segment.y - piece2.segment.y;
				case W:
				default:
					return -(piece1.segment.x - piece2.segment.x);
			}
		}
	};

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
	public void testCollections() {
		TreeSet<RectangleSidePiece> set = new TreeSet<RectangleSidePiece>(
			PIECES_COMPARATOR);
		RectangleArea r1 = new RectangleArea(0, 0, 3, 4);
		RectangleArea r2 = new RectangleArea(4, 0, 3, 4);
		RectangleSidePiece piece = r1.getSideAsSidePiece(Directions.S);
		RectangleSidePiece piece2 = r2.getSideAsSidePiece(Directions.S);
		set.add(piece);
		assertFalse(piece.equals(piece2));
		assertFalse(set.contains(piece2));
	}
	@Test
	public void testCreateRectangle() {
		RectangleArea r = new RectangleArea(5, 6, 7, 8);
		for (CardinalDirection dir : CARDINAL_DIRECTIONS) {
			System.out.println("Testing "+dir);
			RectangleSidePiece piece = r.getSideAsSidePiece(dir);
			assertEquals(new Rectangle(r), new Rectangle(piece.createRectangle(piece.isVertical() ? r.width : r.height)));
		}
	}
}
