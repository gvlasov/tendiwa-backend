package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.OrthoCellSegment;

public final class RasterizationSegment implements OrthoCellSegment {
	private final int x;
	private final int y;
	private final int endX;

	RasterizationSegment(double startX, double endX, int y) {
		this.x = (int) Math.ceil(startX);
		this.y = y;
		this.endX = (int) Math.floor(endX);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int length() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getEndX() {
		return endX;
	}

	@Override
	public Orientation orientation() {
		return Orientation.HORIZONTAL;
	}
}
