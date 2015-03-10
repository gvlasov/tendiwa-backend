package org.tendiwa.geometry;

public final class BasicRectangularHull implements RectangularHull {
	private final double minX;
	private final double minY;
	private final double maxX;
	private final double maxY;

	public BasicRectangularHull(
		double minX,
		double maxX,
		double minY,
		double maxY
	) {
		if (minX > maxX) {
			throw new IllegalArgumentException("minX must be < maxX");
		}
		if (minY > maxY) {
			throw new IllegalArgumentException("minY must be < maxY");
		}
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	@Override
	public double minX() {
		return minX;
	}

	@Override
	public double maxX() {
		return maxX;
	}

	@Override
	public double minY() {
		return minY;
	}

	@Override
	public double maxY() {
		return maxY;
	}
}
