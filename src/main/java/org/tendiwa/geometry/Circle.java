package org.tendiwa.geometry;

public interface Circle {
	Point2D center();

	double radius();

	default double diameter() {
		return radius() * 2;
	}

	default boolean contains(Point2D point) {
		return point.distanceTo(center()) < radius();
	}

	default Rectangle2D bounds() {
		Point2D center = center();
		double diameter = diameter();
		return new BasicRectangle2D(
			center.x() - radius(),
			center.y() - radius(),
			diameter,
			diameter
		);
	}
}
