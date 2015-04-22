package org.tendiwa.geometry;

public abstract class Point2D_Wr implements Point2D {
	private final double x;
	private final double y;

	public Point2D_Wr(Point2D point) {
		this.x = point.x();
		this.y = point.y();
	}

	@Override
	public double x() {
		return x;
	}

	@Override
	public double y() {
		return y;
	}

}
