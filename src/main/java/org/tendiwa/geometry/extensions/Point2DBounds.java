package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.Point2D;

import java.util.Collection;

/**
 * Computes the least rectangle bounding a collection of points.
 */
public final class Point2DBounds {
	public final double minX;
	public final double minY;
	public final double maxX;
	public final double maxY;

	public Point2DBounds(Collection<Point2D> points) {
		double minX = Double.MAX_VALUE,
			minY = Double.MAX_VALUE,
			maxX = Double.MIN_VALUE,
			maxY = Double.MIN_VALUE;
		for (Point2D point : points) {
			if (point.x < minX) {
				minX = point.x;
			}
			if (point.y < minY) {
				minY = point.y;
			}
			if (point.x > maxX) {
				maxX = point.x;
			}
			if (point.y > maxY) {
				maxY = point.y;
			}
		}
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
}

