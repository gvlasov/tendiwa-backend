package org.tendiwa.geometry;

import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Range;

public final class RectangleDynamicRange implements Range {
	private final Rectangle rectangle;
	private final Orientation orientation;

	public RectangleDynamicRange(
		Rectangle rectangle,
		Orientation orientation
	) {
		this.rectangle = rectangle;
		this.orientation = orientation;
	}

	@Override
	public int min() {
		return rectangle.getMinDynamicCoord(orientation);
	}

	@Override
	public int max() {
		return rectangle.getMaxDynamicCoord(orientation);
	}
}
