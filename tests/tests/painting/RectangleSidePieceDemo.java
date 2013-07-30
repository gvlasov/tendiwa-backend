package tests.painting;

import java.awt.Color;

import painting.TestCanvas;
import tendiwa.geometry.Directions;
import tendiwa.geometry.RectangleArea;

public class RectangleSidePieceDemo extends TestCanvas {
	public static void main(String[] args) {
		visualize();
	}
	@Override
	public void paint() {
		setScale(2);
		RectangleArea rectangle = new RectangleArea(5,5,15,5);
		draw(rectangle, Color.RED);
		draw(rectangle.getSideAsSidePiece(Directions.S).createRectangle(4), Color.BLUE);

	}
}
