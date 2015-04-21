package org.tendiwa.geometry;

import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Range;

final class RectangleStaticRange implements Range {
	private final Rectangle rectangle;
	private final Orientation orientation;

	RectangleStaticRange(
		Rectangle rectangle,
		Orientation orientation
	) {
		this.rectangle = rectangle;
		this.orientation = orientation;
	}

	@Override
	public int min() {
		return rectangle.getMinStaticCoord(orientation);
	}

	@Override
	public int max() {
		return rectangle.getMaxStaticCoord(orientation);
	}
}
