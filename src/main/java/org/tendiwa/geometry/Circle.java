package org.tendiwa.geometry;

interface Circle {
	Point2D center();

	double radius();

	default boolean contains(Point2D point) {
		return point.distanceTo(center()) < radius();
	}
}
