package org.tendiwa.geometry;

import java.util.Optional;

@FunctionalInterface
public interface StraightLineIntersection {
	public Optional<Point2D> point();

	public default boolean intersect() {
		return point().isPresent();
	}
}
