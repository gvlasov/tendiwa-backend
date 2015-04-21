package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vectors2D;

final class PointSlidingOnEdge {
	double x;
	double y;

	/**
	 * Sets {@link #x} and {@link #y} to the coordinates of intersection of a line from {@code start} to {@code
	 * end} with a horizontal line on {@code y}-coordinate.
	 *
	 * @param start
	 * 	Start of a segment.
	 * @param end
	 * 	End of a segment.
	 * @param y
	 * 	Y-coordinate of a horizontal line.
	 */
	void setToIntersection(Point2D start, Point2D end, int y) {
		double a = start.y() - end.y();
		double b = end.x() - start.x();
		double c = start.x() * end.y() - end.x() * start.y();
		double horizontalA = 0;
		double horizontalB = 100;
		double horizontalC = -100 * y;
		double zn = Line2D.det(a, b, horizontalA, horizontalB);
		if (Math.abs(zn) < Vectors2D.EPSILON) {
			throw new RuntimeException();
		}
		this.x = -Line2D.det(c, b, horizontalC, horizontalB) / zn;
		this.y = -Line2D.det(a, c, horizontalA, horizontalC) / zn;
	}
}
