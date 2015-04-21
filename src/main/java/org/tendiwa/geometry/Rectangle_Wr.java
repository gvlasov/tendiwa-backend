package org.tendiwa.geometry;

public abstract class Rectangle_Wr implements Rectangle {
	private final Rectangle rectangle;

	public Rectangle_Wr(Rectangle rectangle) {

		this.rectangle = rectangle;
	}

	@Override
	public int x() {
		return rectangle.x();
	}

	@Override
	public int y() {
		return rectangle.y();
	}

	@Override
	public int width() {
		return rectangle.width();
	}

	@Override
	public int height() {
		return rectangle.height();
	}


}
