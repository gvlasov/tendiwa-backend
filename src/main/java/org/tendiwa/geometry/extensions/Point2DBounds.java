package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.*;

import java.util.Collection;

/**
 * Computes the least rectangle bounding a collection of points.
 */
public final class Point2DBounds extends Rectangle2D_Wr implements Rectangle2D {

	public Point2DBounds(Collection<Point2D> points) {
		super(computeBoundingRectangle(points));
	}

	private static Rectangle2D computeBoundingRectangle(Collection<Point2D> points) {
		double minX = Double.MAX_VALUE,
			minY = Double.MAX_VALUE,
			maxX = Double.MIN_VALUE,
			maxY = Double.MIN_VALUE;
		for (Point2D point : points) {
			if (point.x() < minX) {
				minX = point.x();
			}
			if (point.y() < minY) {
				minY = point.y();
			}
			if (point.x() > maxX) {
				maxX = point.x();
			}
			if (point.y() > maxY) {
				maxY = point.y();
			}
		}
		return new BasicRectangle2D(
			minX,
			minY,
			maxX - minX,
			maxY - minY
		);
	}
}

