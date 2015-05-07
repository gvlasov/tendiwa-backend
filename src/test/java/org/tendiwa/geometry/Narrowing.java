package org.tendiwa.geometry;

import org.tendiwa.geometry.extensions.PointTrail;

public final class Narrowing extends Polygon_Wr {

	public Narrowing() {
		super(
			new PointTrail(0, 0)
				.moveBy(2, 2)
				.moveBy(2, -2)
				.moveBy(0, 5)
				.moveBy(-2, -2)
				.moveBy(-2, 2)
				.polygon()
		);
	}
}
