package tests.painting;

import static tendiwa.geometry.Directions.CARDINAL_DIRECTIONS;
import painting.TestCanvas;
import tendiwa.geometry.CardinalDirection;
import tendiwa.geometry.GrowingRectangleSystem;
import tendiwa.geometry.RectangleArea;

public class RectangleSidePieceDemo extends TestCanvas {
	public static void main(String[] args) {
		visualize();
	}
	@Override
	public void paint() {
		setScale(2);
		RectangleArea rectangle = new RectangleArea(12, 17, 70, 67);
		GrowingRectangleSystem rs = new GrowingRectangleSystem(1, rectangle);
		for (CardinalDirection dir : CARDINAL_DIRECTIONS) {
			rs.grow(rectangle, dir, 8, 7, 0);
		}
		for (CardinalDirection dir : CARDINAL_DIRECTIONS) {
			draw(rectangle.getSideAsSidePiece(dir));
		}
		draw(rs);
	}
}
