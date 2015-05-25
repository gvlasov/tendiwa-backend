package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

public abstract class Rectangle2D_Wr implements Rectangle2D {
	private final Rectangle2D rectangle;

	protected Rectangle2D_Wr(Rectangle2D rectangle) {
		this.rectangle = rectangle;
	}

	@Override
	public double x() {
		return rectangle.x();
	}

	@Override
	public double y() {
		return rectangle.y();
	}

	@Override
	public double width() {
		return rectangle.width();
	}

	@Override
	public double height() {
		return rectangle.height();
	}

	@Override
	public ImmutableList<Point2D> toImmutableList() {
		return rectangle.toImmutableList();
	}
}
