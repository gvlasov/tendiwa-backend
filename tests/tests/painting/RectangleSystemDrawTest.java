package tests.painting;

import java.awt.Color;

import tendiwa.drawing.DrawingRectangleSystem;
import tendiwa.drawing.TestCanvas;
import tendiwa.drawing.TestCanvasBuilder;
import tendiwa.geometry.Directions;
import tendiwa.geometry.RandomRectangleSystem;
import tendiwa.geometry.RectangleArea;
import tendiwa.geometry.RectangleSidePiece;
import tendiwa.geometry.RectangleSystem;

public class RectangleSystemDrawTest {
	public static void main(String[] args) {
		TestCanvas canvas = new TestCanvasBuilder()
			.setSize(1280, 1024)
			.setDefaultDrawingAlgorithmForClass(
				RectangleSystem.class,
				DrawingRectangleSystem.withColors(Color.BLACK, Color.DARK_GRAY, Color.LIGHT_GRAY)
				)
			.build();
		RectangleSystem rs = new RandomRectangleSystem(0, 0, 1280, 1024, 7, 0);
		// RectangleSystem rs = new RectangleSystem(0);
		// rs.addRectangleArea(10, 20, 30, 40);
		// RectangleArea r = rs.rectangleSet().iterator().next();
		// rs.splitRectangle(r, Orientation.VERTICAL, 10, false);
		RectangleSidePiece piece = new RectangleArea(20,20,40,40).getSideAsSidePiece(Directions.S);

		canvas.draw(piece, canvas.TOP_LAYER);
		canvas.draw(rs);
	}
}
