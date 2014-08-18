package org.tendiwa.data;

import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;

public final class FourCyclePenisGraph {
	public static GraphConstructor<Point2D, Segment2D> create() {
		return new GraphConstructor<>(Segment2D::new)
			.vertex(0, new Point2D(50, 50))
			.vertex(1, new Point2D(150, 50))
			.vertex(2, new Point2D(50, 150))
			.vertex(3, new Point2D(150, 150))
			.vertex(4, new Point2D(200, 150))
			.vertex(5, new Point2D(200, 300))
			.vertex(6, new Point2D(350, 150))
			.vertex(7, new Point2D(350, 300))
			.vertex(8, new Point2D(32, 245))
			.vertex(9, new Point2D(108, 214))
			.vertex(10, new Point2D(152, 298))
			.vertex(11, new Point2D(67, 347))
			.edge(1, 4)
			.cycle(0, 1, 3, 2)
			.cycle(8, 9, 10, 11)
			.edge(10, 5)
			.edge(9, 2)
			.cycle(4, 5, 7, 6);
	}
}
