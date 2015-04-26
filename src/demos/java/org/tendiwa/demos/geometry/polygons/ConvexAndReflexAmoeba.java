package org.tendiwa.demos.geometry.polygons;

import org.tendiwa.geometry.BasicPolygon;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Polygon_Wr;

import java.util.ArrayList;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public final class ConvexAndReflexAmoeba extends Polygon_Wr {
	public ConvexAndReflexAmoeba() {
		super(
			new BasicPolygon(
				point2D(11, 14),
				point2D(26, 61),
				point2D(12, 92),
				point2D(78, 102),
				point2D(8, 166),
				point2D(62, 161),
				point2D(93, 185),
				point2D(125, 168),
				point2D(177, 186),
				point2D(160, 138),
				point2D(193, 122),
				point2D(142, 101),
				point2D(179, 91),
				point2D(147, 59),
				point2D(178, 6),
				point2D(89, 54),
				point2D(100, 13)
			)
		);
	}
}
