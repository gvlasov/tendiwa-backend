package org.tendiwa.geometry;

import org.tendiwa.core.Directions;

public final class RectanglePolygon extends Polygon_Wr {
	public RectanglePolygon(Rectangle2D rectangle) {
		super(
			new BasicPolygon(
				rectangle.corner(Directions.NW),
				rectangle.corner(Directions.NE),
				rectangle.corner(Directions.SE),
				rectangle.corner(Directions.SW)
			)
		);
	}
}
