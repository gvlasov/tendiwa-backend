package tests.painting;

import java.awt.Point;
import java.awt.Rectangle;

import tendiwa.core.meta.Range;
import tendiwa.drawing.TestCanvas;
import tendiwa.drawing.TestCanvasBuilder;
import tendiwa.geometry.InterrectangularPath;
import tendiwa.geometry.WaveRectangleSystem;
import tendiwa.geometry.RectangleArea;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.RectangleSystem.NeighboursIterable;

public class WaveRectangleSystemDemo {

	public static void main(String[] args) {
		RectangleSystem rs = new WaveRectangleSystem(0, new Range(5, 5), 1, new Point(10, 12));
//		RectangleArea rMostNighbours = rs.findRectangleWithMostNeigbors();
//		NeighboursIterable neighbors = rs.getNeighboursIterable(rMostNighbours);
//		neighbors.setRandomStartingNeighbour();
//		neighbors.setClockwiseOrder();
//		InterrectangularPath trail = new InterrectangularPath(rs, 1);
//		for (RectangleArea r : neighbors) {
//			trail.addNextRectangle(r);
//		}
//		draw(trail);
	}

}
