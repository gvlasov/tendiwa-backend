package org.tendiwa.core.meta;

import org.tendiwa.core.Direction;
import org.tendiwa.core.OrdinalDirection;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.*;

import java.util.Objects;

public interface Cell {
	static final double SQRT_2 = Math.sqrt(2);

	int x();

	int y();

	/**
	 * Finds Chebyshev distance between this cell and another cell.
	 *
	 * @param cell
	 * 	Another cell.
	 * @return Chebyshev distance between two cells.
	 */
	default int chebyshevDistanceTo(Cell cell) {
		return Math.max(
			Math.abs(cell.x() - x()),
			Math.abs(cell.y() - y())
		);
	}

	default int distanceInt(int x, int y) {
		return (int) Math.sqrt(Math.pow(x - x(), 2) + Math.pow(y - y(), 2));
	}

	default double distanceDouble(int x, int y) {
		return Math.sqrt(Math.pow(x - x(), 2) + Math.pow(y - y(), 2));
	}

	default boolean isNear(int x, int y) {
		int ableX = Math.abs(x() - x);
		int ableY = Math.abs(y() - y);
		return (ableX == 1 && ableY == 0) || (ableY == 1 && ableX == 0) || (ableY == 1 && ableX == 1);
	}

	/**
	 * Returns a static coord if this point was a part of a line with given orientation.
	 *
	 * @param orientation
	 * @return this.x if orientation is {@link Orientation#VERTICAL}, or this.y if orientation is {@link
	 * Orientation#HORIZONTAL}
	 */
	default int getStaticCoord(Orientation orientation) {
		if (orientation.isVertical()) {
			return x();
		} else {
			return y();
		}
	}

	/**
	 * Returns a dynamic coord if this point was a part of a line with given orientation.
	 *
	 * @param orientation
	 * @return this.x if orientation is {@link Orientation#HORIZONTAL}, or this.y if orientation is {@link
	 * Orientation#VERTICAL}
	 */
	default int getDynamicCoord(Orientation orientation) {
		if (orientation.isHorizontal()) {
			return x();
		} else {
			return y();
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
	default Cell newRelativeCell(int dx, int dy) {
		return new BasicCell(
			x() + dx,
			y() + dy
		);
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
	default Cell moveToSide(Direction direction, int cells) {
		int[] d = direction.side2d();
		return new BasicCell(x() + d[0] * cells, y() + d[1] * cells);
	}

	/**
	 * Returns 1 if cells have equal x or y values, otherwise returns {@code Math.sqrt(2)}.
	 *
	 * @param neighbor
	 * 	Another cell.
	 * @return 1 or {@code Math.sqrt(2)}.
	 */
	default double diagonalComponent(Cell neighbor) {
		return x() - neighbor.x() == 0 || y() - neighbor.y() == 0 ? 1 : SQRT_2;
	}

	default double quickDistance(Cell c2) {
		int dx = Math.abs(x() - c2.x());
		int dy = Math.abs(y() - c2.y());
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

	default Point2D toPoint() {
		return new BasicPoint2D(x(), y());
	}

	/**
	 * Takes a cell, and creates a Rectangle with one corner in this cell, and another corner in some other cell.
	 * <p>
	 * The advantage of this method over a plain {@link org.tendiwa.geometry.Rectangle} constructor is that this
	 * method allows more intuitive descriptions of a rectangle (the constructor creates rectangles by their NW
	 * corner, whereas with this method you can create a rectangle counting from any corner).
	 *
	 * @param x
	 * 	X-coordinate of the first corner.
	 * @param y
	 * 	Y-coordinate of the first corner.
	 * @param anotherCornerDirection
	 * 	Location of the second point relatively from the initial point.
	 * @param width
	 * 	How far is the second point from the initial point on the x-axis.
	 * @param height
	 * 	How far is the second point from the initial point on the y-axis.
	 * @return A new rectangle grown from the point in the specified direction.
	 */
	default Rectangle growRectangle(
		OrdinalDirection anotherCornerDirection,
		int width,
		int height
	) {
		Objects.requireNonNull(anotherCornerDirection);
		switch (anotherCornerDirection) {
			case SE:
				return new BasicRectangle(
					x(),
					y(),
					width,
					height
				);
			case NE:
				return new BasicRectangle(
					x(),
					y() - height + 1,
					width,
					height
				);
			case NW:
				return new BasicRectangle(
					x() - width + 1,
					y() - height + 1,
					width,
					height
				);
			case SW:
			default:
				return new BasicRectangle(
					x() - width + 1,
					y(),
					width,
					height
				);
		}
	}

	default Rectangle centerRectangle(int width, int height) {
		return new BasicRectangle(
			x() - width / 2,
			y() - height / 2,
			width,
			height
		);
	}
}
