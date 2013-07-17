package tests.painting;

import painting.TestCanvas;
import tendiwa.geometry.Directions;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.GrowingRectangleSystem;

public class GrowingRectangleSystemDrawTest extends TestCanvas {

	public void paint() {
		EnhancedRectangle er = new EnhancedRectangle(100, 100, 30, 30);
		GrowingRectangleSystem grs = new GrowingRectangleSystem(0, er);
		grs.grow(er, Directions.N, 100, 16, 0);
		grs.grow(er, Directions.E, 12, 16, 0);
		grs.grow(er, Directions.S, 12, 16, 0);
		grs.grow(er, Directions.W, 12, 16, 0);
		draw(grs);
	}
}
