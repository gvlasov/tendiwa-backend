package org.tendiwa.core;

import org.tendiwa.geometry.BasicCell;

public class Border {
	public final int x;
	public final int y;
	public final CardinalDirection side;

	/**
	 * Transfroms border from side {@code side} in cell x:y to equivalent border coordinates where {@code side} is
	 * either
	 * {@link Directions#N} of {@link Directions#W}, and shifts {@code x} and {@code y} accordingly.
	 *
	 * @param x
	 * 	X coordinate of a cell in world coordinates.
	 * @param y
	 * 	Y coordinate of a cell in world coordinates.
	 * @param side
	 * 	Side of a cell where border is.
	 */
	public Border(int x, int y, CardinalDirection side) {
		assert side != null;
		if (side != Directions.N && side != Directions.W) {
			if (side == Directions.E) {
				side = Directions.W;
				x += 1;
			} else {
				assert side == Directions.S;
				side = Directions.N;
				y += 1;
			}
		}
		this.x = x;
		this.y = y;
		this.side = side;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Border that = (Border) o;

		if (x != that.x) return false;
		if (y != that.y) return false;
		if (side != that.side) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + (side != null ? side.hashCode() : 0);
		return result;
	}

	public BasicCell toPoint() {
		return new BasicCell(x, y);
	}

	@Override
	public String toString() {
		return "Border{" +
			"side=" + side +
			", x=" + x +
			", y=" + y +
			'}';
	}
}
