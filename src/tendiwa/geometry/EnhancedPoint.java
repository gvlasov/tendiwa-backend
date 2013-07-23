package tendiwa.geometry;

import java.awt.Point;


public class EnhancedPoint extends Point {
	private static final long serialVersionUID = -437683005315402667L;

	public void moveToSide(Direction side) {
		int[] d = side.side2d();
		x += d[0];
		y += d[1];
	}
}
