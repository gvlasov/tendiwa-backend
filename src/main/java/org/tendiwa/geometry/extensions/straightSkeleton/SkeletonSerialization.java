package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Lists;
import org.tendiwa.geometry.Point2D;

import java.util.List;

public final class SkeletonSerialization {
	private static void outputPoints(List<Point2D> vertices) {
		for (Point2D point : Lists.reverse(vertices)) {
			System.out.println("new Point2D(" + point.x + ", " + point.y + "),");
		}
	}
}
