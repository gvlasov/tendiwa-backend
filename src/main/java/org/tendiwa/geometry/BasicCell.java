package org.tendiwa.geometry;

import org.tendiwa.core.Direction;
import org.tendiwa.core.meta.Cell;

/**
 * Cell is a square 1×1 units large with integer coordinates.
 * <p>
 * Cells are more useful and optimal than {@link org.tendiwa.geometry.Point2D}s when operating on coordinates of
 * objects
 * in game word.
 * <p>
 * Cell introduces several useful methods over a similar {@link Point2D} class, as well as introduces a new concept of
 * <b>dynamic coordinate</b> and <b>static coordinate</b>. They are the somewhat the same as x-coordinate and
 * y-coordinate.
 * <p>
 * X-coordinate is a horizontal dynamic coordinate, and it is a vertical static coordinate.
 * <p>
 * On the contrary,  y-coordinate is a vertical dynamic coordinate and a horizontal static coordinate.
 * <p>
 * Think of it the following way: if you take a horizontal line consisting of points, each point will have the same
 * y-coordinate (hence y is horizontal static) and different x coordinate (so x is horizontal dynamic)
 */
public final class BasicCell implements Cell {
	public final int x;
	public final int y;

	public BasicCell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a cell that would contain {@code point}, i.e. with coordinates of a point rounded down;
	 *
	 * @param point
	 */
	public BasicCell(Point2D point) {
		this.x = (int) Math.floor(point.x());
		this.y = (int) Math.floor(point.y());
	}

	public BasicCell(BasicCell point) {
		this.x = point.x;
		this.y = point.y;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BasicCell cell = (BasicCell) o;

		if (x != cell.x) return false;
		if (y != cell.y) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}


	@Override
	public String toString() {
		return x + ":" + y;
	}

	/**
	 * Creates a new cell, moved from the original one.
	 *
	 * @param direction
	 * 	Direction to move.
	 * @return The same mutated point.
	 */
	public BasicCell moveToSide(Direction direction) {
		int[] d = direction.side2d();
		return new BasicCell(x + d[0], y + d[1]);
	}


	public Cell newRelativePoint(Direction dir) {
		int[] coords = dir.side2d();
		return new BasicCell(x + coords[0], y + coords[1]);
	}


	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	public Point2D asPoint() {
		return new BasicPoint2D(x, y);
	}
}
