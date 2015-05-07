package org.tendiwa.geometry;

import org.tendiwa.geometry.extensions.PointTrail;

public final class IsoscelesTriangle extends Polygon_Wr {

	public IsoscelesTriangle(double base, double height) {
		super(
			new PointTrail(0, 0)
				.moveByX(base)
				.moveBy(base / 2, height)
				.polygon()
		);
	}
}
