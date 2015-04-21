package org.tendiwa.geometry;

import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.BasicRange;
import org.tendiwa.core.meta.Range;

public final class GeometryPrimitives {
	public static Point2D point2D(double x, double y) {
		return new BasicPoint2D(x, y);
	}

	public static Segment2D segment2D(double x1, double y1, double x2, double y2) {
		return new BasicSegment2D(
			new BasicPoint2D(x1, y1),
			new BasicPoint2D(x2, y2)
		);
	}

	public static Segment2D segment2D(Point2D start, Point2D end) {
		return new BasicSegment2D(start, end);
	}

	public static Rectangle2D rectangle2D(double x, double y, double width, double height) {
		return new BasicRectangle2D(x, y, width, height);
	}

	public static Rectangle2D rectangle2D(double width, double height) {
		return new BasicRectangle2D(0, 0, width, height);
	}

	public static Rectangle rectangle(int x, int y, int width, int height) {
		return new BasicRectangle(x, y, width, height);
	}

	public static Rectangle rectangle(int width, int height) {
		return new BasicRectangle(0, 0, width, height);
	}

	public static Range range(int min, int max) {
		return new BasicRange(min, max);
	}

	public static OrthoCellSegment orthoCellSegment(int x, int y, int length, Orientation orientation) {
		return new BasicOrthoCellSegment(x, y, length, orientation);
	}
}
