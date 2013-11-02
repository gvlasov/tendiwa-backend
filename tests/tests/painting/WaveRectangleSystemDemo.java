package tests.painting;

import tendiwa.core.meta.Range;
import tendiwa.core.EnhancedRectangle;
import tendiwa.core.RectangleSystem;
import tendiwa.core.WaveRectangleSystem;

import java.io.IOException;

public class WaveRectangleSystemDemo {

public static void main(String[] args) throws IOException {
	RectangleSystem irs = new RectangleSystem(0);
	// Е
//		irs.addRectangleArea(8, 10, 4, 20);
//		irs.addRectangleArea(12, 10, 10, 3);
//		irs.addRectangleArea(12, 18, 10, 3);
//		irs.addRectangleArea(12, 27, 10, 3);
//		// Л
//		irs.addRectangleArea(23, 10, 4, 20);
//		irs.addRectangleArea(27, 10, 2, 3);
//		irs.addRectangleArea(28, 13, 4, 5);
//		irs.addRectangleArea(30, 18, 4, 12);
//		// Л
//		irs.addRectangleArea(40, 10, 4, 20);
//		irs.addRectangleArea(44, 10, 2, 3);
//		irs.addRectangleArea(45, 13, 4, 5);
//		irs.addRectangleArea(47, 18, 4, 12);
	irs.addRectangle(new EnhancedRectangle(10, 10, 1, 1));
	irs.addRectangle(new EnhancedRectangle(13, 12, 1, 1));
	irs.addRectangle(new EnhancedRectangle(13, 14, 70, 1));
	int tries = 1;
	for (int i = 0; i < (WaveRectangleSystem.DEBUG ? 1 : tries); i++) {
		WaveRectangleSystem rs = new WaveRectangleSystem(
				0,
				new Range(7, 20),
				irs);
		if (tries == 1) {
			rs.canvas.show();
		}
	}

	// RectangleArea rMostNighbours = rs.findRectangleWithMostNeigbors();
	// NeighboursIterable neighbors =
	// rs.getNeighboursIterable(rMostNighbours);
	// neighbors.setRandomStartingNeighbour();
	// neighbors.setClockwiseOrder();
	// InterrectangularPath trail = new InterrectangularPath(rs, 1);
	// for (RectangleArea r : neighbors) {
	// trail.addNextRectangle(r);
	// }
	// drawWorld(trail);
}

}
