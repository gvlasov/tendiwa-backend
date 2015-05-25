package org.tendiwa.graphs;

import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.graphs.graphs2d.ConstructedGraph2D;

final class TwoCycles extends ConstructedGraph2D {
	TwoCycles() {
		addPolygon(
			new PointTrail(100, 100)
				.moveByY(100)
				.moveBy(-50, 50)
				.moveBy(-50, -50)
				.moveByY(-50)
				.polygon()
		);
		addPolygon(
			new PointTrail(100, 100)
				.moveByY(100)
				.moveBy(50, 50)
				.moveBy(50, -50)
				.moveByY(50)
				.polygon()
		);
	}
}
