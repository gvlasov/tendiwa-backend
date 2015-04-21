package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Orientation;

final class BasicRectangleSide implements Side {
	private final Rectangle rectangle;
	private final CardinalDirection side;

	public BasicRectangleSide(Rectangle rectangle, CardinalDirection side) {
		this.rectangle = rectangle;
		this.side = side;
	}

	@Override
	public int length() {
		return side.isVertical() ?
			rectangle.width() :
			rectangle.height();
	}

	@Override
	public int getX() {
		return side == CardinalDirection.E ?
			rectangle.maxX() :
			rectangle.x();
	}

	@Override
	public int getY() {
		return side == CardinalDirection.S ?
			rectangle.maxY() :
			rectangle.y();
	}

	@Override
	public Orientation orientation() {
		return side.getOrientation().reverted();
	}

	@Override
	public CardinalDirection face() {
		return side;
	}
}
