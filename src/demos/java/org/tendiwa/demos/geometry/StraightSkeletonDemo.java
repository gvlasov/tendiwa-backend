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
			add(new Point2D(10, 10));
			add(new Point2D(40, 5));
			add(new Point2D(60, 20));
			add(new Point2D(70, 40));
			add(new Point2D(30, 180));
			add(new Point2D(5, 30));
		}};
		StraightSkeleton skeleton = new StraightSkeleton(vertices);
	}
}
