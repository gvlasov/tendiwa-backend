package org.tendiwa.geometry;

public final class BasicPoint2D implements Point2D {
	private final double x;
	private final double y;

	public BasicPoint2D(double x, double y) {
		assert !Double.isNaN(x);
		assert !Double.isNaN(y);
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + ":" + y + "]";
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BasicPoint2D that = (BasicPoint2D) o;

		if (Double.compare(that.x, x) != 0) return false;
		if (Double.compare(that.y, y) != 0) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

}
