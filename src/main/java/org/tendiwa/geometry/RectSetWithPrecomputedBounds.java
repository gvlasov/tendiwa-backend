package org.tendiwa.geometry;

final class RectSetWithPrecomputedBounds extends RectSet_Wr {

	private final Rectangle bounds;

	RectSetWithPrecomputedBounds(RectSet rectSet, Rectangle bounds) {
		super(rectSet);
		this.bounds = bounds;
	}

	@Override
	public Rectangle bounds() {
		return bounds;
	}
}
