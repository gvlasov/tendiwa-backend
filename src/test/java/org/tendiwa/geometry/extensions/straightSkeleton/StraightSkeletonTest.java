package org.tendiwa.geometry.extensions.straightSkeleton;

import org.junit.Test;
import org.tendiwa.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class StraightSkeletonTest {
	@Test
	public void nonConvexPolygonWithoutHoles() {
		List<Point2D> vertices = new ArrayList<Point2D>() {{
			add(new Point2D(4, 30));
			add(new Point2D(41, 41));
			add(new Point2D(48, 11));
			add(new Point2D(95, 47));
			add(new Point2D(135, 10));
			add(new Point2D(156, 25));
			add(new Point2D(182, 10));
			add(new Point2D(197, 52));
			add(new Point2D(149, 80));
			add(new Point2D(180, 135));
			add(new Point2D(164, 195));
			add(new Point2D(133, 180));
			add(new Point2D(119, 195));
			add(new Point2D(87, 143));
			add(new Point2D(15, 190));
			add(new Point2D(31, 123));
			add(new Point2D(7, 112));
			add(new Point2D(50, 83));
		}};
		new StraightSkeleton(vertices);
	}
}
