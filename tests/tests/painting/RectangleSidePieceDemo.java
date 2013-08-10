package tests.painting;

import java.awt.Color;

import tendiwa.drawing.DrawingRectangle;
import tendiwa.drawing.TestCanvas;
import tendiwa.drawing.TestCanvasBuilder;
import tendiwa.geometry.Directions;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.RectangleArea;

public class RectangleSidePieceDemo {
	public static void main(String[] args) {
		TestCanvas canvas = new TestCanvasBuilder().setScale(2).build();
		RectangleArea r1 = new RectangleArea(5,5,15,5);
		EnhancedRectangle r2 = r1.getSideAsSidePiece(Directions.S).createRectangle(4);
		canvas.draw(r1);
		canvas.draw(r2, DrawingRectangle.withColor(Color.YELLOW));
		canvas.draw(r1.getSideAsSidePiece(Directions.S));
		canvas.draw(r2.getSideAsSidePiece(Directions.N));
	}
}
