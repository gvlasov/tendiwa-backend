package tests.painting;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.stream.FileImageOutputStream;

import tendiwa.core.meta.GifSequenceWriter;
import tendiwa.core.meta.Range;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.WaveRectangleSystem;

public class WaveRectangleSystemDemo {

	public static void main(String[] args) throws IIOException, FileNotFoundException, IOException {
		RectangleSystem irs = new RectangleSystem(0);
		irs.addRectangleArea(10, 20, 3, 8);
		irs.addRectangleArea(16, 18, 5, 14);
		irs.addRectangleArea(22, 18, 6, 14);
		WaveRectangleSystem rs = new WaveRectangleSystem(
			0,
			new Range(1, 5),
			1,
			irs);
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
