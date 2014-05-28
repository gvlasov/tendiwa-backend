package org.tendiwa.graphs;

import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

public class MinimumCycleBasisTest {
	@Test
	public void testPerpDotProduct() {
		Segment2D a = new Segment2D(
			new Point2D(20, 60),
			new Point2D(60, 60)
		);
		Segment2D b = new Segment2D(
			new Point2D(60, 60),
			new Point2D(80, 80)
		);
		double pdp = MinimumCycleBasis.perpDotProduct(
			new double[]{a.dx(), a.dy()},
			new double[]{b.dx(), b.dy()}
		);
		System.out.println(pdp);
	}
}
