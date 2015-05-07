package org.tendiwa.geometry;

import org.tendiwa.geometry.extensions.PointTrail;

public final class Parallelogram extends Polygon_Wr {
	public Parallelogram(double a) {
		super(
			new PointTrail(0, 0)
				.moveByX(a)
				.moveBy(a, a)
				.moveByX(-a)
				.polygon()
		);
	}
}
