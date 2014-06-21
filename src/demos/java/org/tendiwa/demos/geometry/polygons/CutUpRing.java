package org.tendiwa.demos.geometry.polygons;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PointTrail;

import java.util.ArrayList;
import java.util.List;

public class CutUpRing extends ArrayList<Point2D> {
	public CutUpRing() {
		List<Point2D> points = new PointTrail(20, 20)
			.moveBy(40, 0)
			.moveBy(0, 20)
			.moveBy(-20, 0)
			.moveBy(0, 20)
			.moveBy(40, 0)
			.moveBy(0, -20)
			.moveBy(-20, 0)
			.moveBy(0, -20)
			.moveBy(40, 0)
			.moveBy(0, 60)
			.moveBy(-80, 0)
			.points();
		addAll(points);
	}
}
