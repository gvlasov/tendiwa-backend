package tests.painting;

import painting.TestCanvas;
import tendiwa.geometry.InterrectangularPath;
import tendiwa.geometry.RandomGrowingRectangleSystem;
import tendiwa.geometry.RectangleArea;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.RectangleSystem.NeighboursIterable;

public class RectangleSystemClockwiseIterationsDemo extends TestCanvas {
	public static void main(String[] args) {
		visualize();
	}

	@Override
	public void paint() {
		RectangleSystem rs = new RandomGrowingRectangleSystem(0, 5, 20);
		RectangleArea rMostNighbours = rs.findRectangleWithMostNeigbors();
		NeighboursIterable neighbors = rs.getNeighboursIterable(rMostNighbours);
		neighbors.setRandomStartingNeighbour();
		neighbors.setClockwiseOrder();
		InterrectangularPath trail = new InterrectangularPath(rs, 1);
		for (RectangleArea r : neighbors) {
			trail.addNextRectangle(r);
		}
		draw(trail);
	}

}
