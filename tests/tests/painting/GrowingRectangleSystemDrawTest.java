package tests.painting;

import tendiwa.drawing.TestCanvas;
import tendiwa.geometry.Directions;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.GrowingRectangleSystem;
import tendiwa.geometry.RandomRectangleSystem;
import tendiwa.geometry.RectangleSystem;

public class GrowingRectangleSystemDrawTest {
	public static void main(String[] args) {
		TestCanvas canvas = TestCanvas.builder().setScale(3).build();
		EnhancedRectangle er = new EnhancedRectangle(100, 100, 30, 30);
		GrowingRectangleSystem grs = new GrowingRectangleSystem(0, er);
		grs.grow(er, Directions.N, 100, 16, 0);
		grs.grow(er, Directions.E, 12, 16, 0);
		grs.grow(er, Directions.S, 12, 16, 0);
		grs.grow(er, Directions.W, 12, 16, 0);
		canvas.draw(grs);
		RectangleSystem rs = new RandomRectangleSystem(0, 0, 100, 200, 3, 0);
		canvas.draw(rs);
	}
}
