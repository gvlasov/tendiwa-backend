package tests.painting;

import painting.TestCanvas;
import tendiwa.core.meta.Side;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.GrowingRectangleSystem;

public class GrowingRectangleSystemDrawTest extends TestCanvas {

	public void paint() {
		EnhancedRectangle er = new EnhancedRectangle(100, 100, 30, 30);
		GrowingRectangleSystem grs = new GrowingRectangleSystem(0, er);
		grs.grow(er, Side.N, 100, 16, 0);
		grs.grow(er, Side.E, 12, 16, 0);
		grs.grow(er, Side.S, 12, 16, 0);
		grs.grow(er, Side.W, 12, 16, 0);
		draw(grs);
	}
}
