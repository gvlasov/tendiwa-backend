package org.tendiwa.geometry;

public interface BoundedShape extends Iterable<Point2D> {

	default Rectangle2D bounds() {
		double minX = Integer.MAX_VALUE;
		double minY = Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		double maxY = Integer.MIN_VALUE;
		for (Point2D point : this) {
			if (minX > point.x()) {
				minX = point.x();
			}
			if (minY > point.y()) {
				minY = point.y();
			}
			if (maxX < point.x()) {
				maxX = point.x();
			}
			if (maxY < point.y()) {
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
	default Rectangle integerBounds() {
		double minX = Integer.MAX_VALUE;
		double minY = Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		double maxY = Integer.MIN_VALUE;
		for (Point2D point : this) {
			if (minX > point.x()) {
				minX = point.x();
			}
			if (minY > point.y()) {
				minY = point.y();
			}
			if (maxX < point.x()) {
				maxX = point.x();
			}
			if (maxY < point.y()) {
				maxY = point.y();
			}
		}
		return new BasicRectangle(
			(int) Math.floor(minX),
			(int) Math.floor(minY),
			(int) Math.ceil(maxX - minX) + 1,
			(int) Math.ceil(maxY - minY) + 1
		);
	}
}
