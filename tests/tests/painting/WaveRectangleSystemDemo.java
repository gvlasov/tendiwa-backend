package tests.painting;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOException;

import tendiwa.core.meta.Range;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.WaveRectangleSystem;

public class WaveRectangleSystemDemo {

	public static void main(String[] args) throws IIOException, FileNotFoundException, IOException {
		RectangleSystem irs = new RectangleSystem(0);
		irs.addRectangleArea(10, 20, 4, 14);
		irs.addRectangleArea(18, 16, 4, 14);
		for (int i=0; i<1000; i++) {
			System.out.println(i);
			WaveRectangleSystem rs = new WaveRectangleSystem(
				0,
				new Range(1, 5),
				1,
				irs);
//			rs.canvas.show();
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
		// draw(trail);
	}

}
