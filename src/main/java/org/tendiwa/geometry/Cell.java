package org.tendiwa.geometry;

import org.tendiwa.core.Direction;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.CellPosition;

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
public class Cell implements CellPosition {
	public final int x;
	public final int y;
	private static final double SQRT_2 = Math.sqrt(2);

	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}


	/**
	 * Creates a cell that would contain {@code point}, i.e. with coordinates of a point rounded down;
	 *
	 * @param point
	 */
	public Cell(Point2D point) {
		this.x = (int) Math.floor(point.x);
		this.y = (int) Math.floor(point.y);
	}

	/**
	 * Finds Chebyshev distance between this cell and another cell.
	 *
	 * @param cell
	 * 	Another cell.
	 * @return Chebyshev distance between two cells.
	 */
	public int chebyshevDistanceTo(Cell cell) {
		return Math.max(Math.abs(cell.x - x), Math.abs(cell.y - y));
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Cell cell = (Cell) o;

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

	public Cell(Cell point) {
		this.x = point.x;
		this.y = point.y;
	}

	public int distanceInt(int x, int y) {
		return (int) Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
	}

	public double distanceDouble(int x, int y) {
		return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
	}

	public double distanceDouble(Cell cell) {
		return Math.sqrt(Math.pow(cell.x - x, 2) + Math.pow(cell.y - y, 2));
	}

	public boolean isNear(int x, int y) {
		int ableX = Math.abs(this.x - x);
		int ableY = Math.abs(this.y - y);
		return (ableX == 1 && ableY == 0) || (ableY == 1 && ableX == 0) || (ableY == 1 && ableX == 1);
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
	public Cell moveToSide(Direction direction) {
		int[] d = direction.side2d();
		return new Cell(x + d[0], y + d[1]);
	}

	/**
	 * Creates a new cell, moved from the original one.
	 *
	 * @param direction
	 * 	Direction to move
	 * @param cells
	 * 	How far to move in cells
	 * @return The same mutated point.
	 */
	public Cell moveToSide(Direction direction, int cells) {
		int[] d = direction.side2d();
		return new Cell(x + d[0] * cells, y + d[1] * cells);
	}

	/**
	 * Returns a static coord if this point was a part of a line with given orientation.
	 *
	 * @param orientation
	 * @return this.x if orientation is {@link Orientation#VERTICAL}, or this.y if orientation is {@link
	 * Orientation#HORIZONTAL}
	 */
	public int getStaticCoord(Orientation orientation) {
		if (orientation.isVertical()) {
			return x;
		} else {
			return y;
		}
	}

	/**
	 * Returns a dynamic coord if this point was a part of a line with given orientation.
	 *
	 * @param orientation
	 * @return this.x if orientation is {@link Orientation#HORIZONTAL}, or this.y if orientation is {@link
	 * Orientation#VERTICAL}
	 */
	public int getDynamicCoord(Orientation orientation) {
		if (orientation.isHorizontal()) {
			return x;
		} else {
			return y;
		}
	}

	/**
	 * Creates a new Cell relative to this point.
	 *
	 * @param dx
	 * 	Shift by x-axis.
	 * @param dy
	 * 	Shift by y-axis.
	 * @return New Cell.
	 */
	public Cell newRelativePoint(int dx, int dy) {
		return new Cell(x + dx, y + dy);
	}

	public Cell newRelativePoint(Direction dir) {
		int[] coords = dir.side2d();
		return new Cell(x + coords[0], y + coords[1]);
	}

	/**
	 * Creates a new Cell relative to this point.
	 *
	 * @param dStatic
	 * 	Shift by static axis.
	 * @param dDynamic
	 * 	Shift by dynamic axis.
	 * @param orientation
	 * 	Orientation that determines which axis is dynamic or static.
	 * @return New Cell.
	 * @see Cell For explanation of what static and dynamic axes are.
	 */
	public Cell newRelativePointByOrientaton(int dStatic, int dDynamic, Orientation orientation) {
		if (orientation.isHorizontal()) {
			return new Cell(x + dDynamic, y + dStatic);
		}
		return new Cell(x + dStatic, y + dDynamic);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public double quickDistance(Cell c2) {
		int dx = Math.abs(x - c2.x);
		int dy = Math.abs(y - c2.y);
		int min, max;
		if (dx > dy) {
			min = dy;
			max = dx;
		} else {
			min = dx;
			max = dy;
		}
		return max + min * 0.41421356;
	}

	/**
	 * Returns 1 if cells have equal x or y values, otherwise returns {@code Math.sqrt(2)}.
	 *
	 * @param neighbor
	 * 	Another cell.
	 * @return 1 or {@code Math.sqrt(2)}.
	 */
	public double diagonalComponent(Cell neighbor) {
		return x - neighbor.x == 0 || y - neighbor.y == 0 ? 1 : SQRT_2;
	}

	public Point2D asPoint() {
		return new Point2D(x, y);
	}
}
