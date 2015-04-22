package org.tendiwa.geometry;

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
}
