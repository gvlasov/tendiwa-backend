package org.tendiwa.geometry;

import org.tendiwa.geometry.extensions.PointTrail;

public final class Square extends Polygon_Wr {

	public Square(double sideOfSquare) {
		super(
			new PointTrail(0, 0)
				.moveByX(sideOfSquare)
				.moveByY(sideOfSquare)
				.moveByX(-sideOfSquare)
				.polygon()
		);
	}
}
