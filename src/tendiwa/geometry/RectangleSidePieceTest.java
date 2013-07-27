package tendiwa.geometry;

import static org.junit.Assert.assertEquals;
import static tendiwa.geometry.Directions.E;
import static tendiwa.geometry.Directions.W;

import org.junit.Test;

public class RectangleSidePieceTest {

	@Test
	public void testStaticCoordinateInsideAndOutside() {
		RectangleArea r = new RectangleArea(5, 8, 5, 5);
		int staticCoordinateInside = r.getSideAsSidePiece(E).getLine().getStaticCoordFromSide(W);
		int staticCoordinateOutside = r.getSideAsSidePiece(E).getLine().getStaticCoordFromSide(E);
		assertEquals(staticCoordinateInside, staticCoordinateOutside-1);
		System.out.println(staticCoordinateInside);
	}
	
}
