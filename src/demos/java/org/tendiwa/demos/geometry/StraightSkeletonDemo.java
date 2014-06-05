package org.tendiwa.demos.geometry;

import org.tendiwa.demos.Demos;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.straightSkeleton.StraightSkeleton;

import java.util.ArrayList;
import java.util.List;

public class StraightSkeletonDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(StraightSkeletonDemo.class);
	}

	@Override
	public void run() {
		List<Point2D> vertices = new ArrayList<Point2D>() {{
			add(new Point2D(11, 14));
			add(new Point2D(26, 61));
			add(new Point2D(12, 92));
			add(new Point2D(78, 102));
			add(new Point2D(8, 166));
			add(new Point2D(62, 161));
			add(new Point2D(93, 185));
			add(new Point2D(125, 168));
			add(new Point2D(177, 186));
			add(new Point2D(160, 138));
			add(new Point2D(193, 122));
			add(new Point2D(142, 101));
			add(new Point2D(179, 91));
			add(new Point2D(147, 59));
			add(new Point2D(178, 6));
			add(new Point2D(89, 54));
			add(new Point2D(100, 13));
		}};
		StraightSkeleton skeleton = new StraightSkeleton(vertices);
	}
}
