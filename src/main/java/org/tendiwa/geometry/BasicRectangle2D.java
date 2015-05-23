package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public final class BasicRectangle2D implements Rectangle2D {
	public final double x;
	public final double y;
	public final double width;
	public final double height;

	public BasicRectangle2D(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public double x() {
		return x;
	}

	@Override
	public double y() {
		return y;
	}

	@Override
	public double width() {
		return width;
	}

	@Override
	public double height() {
		return height;
	}


	@Override
	public String toString() {
		return "Rectangle at " + x + ":" + y + ", " + width + "×" + height;
	}

	@Override
	public Iterator<Point2D> iterator() {
		return
			ImmutableList.of(
				point2D(x(), y()),
				point2D(maxX(), y()),
				point2D(maxX(), maxY()),
				point2D(x(), maxY())
			).iterator();
	}
}
