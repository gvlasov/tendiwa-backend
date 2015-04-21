package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Cell;

import java.util.Iterator;

public final class RectangleBorder implements FiniteCellSet {
	private final Rectangle rectangle;

	public RectangleBorder(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	@Override
	public boolean contains(int x, int y) {
		return x == rectangle.x()
			|| y == rectangle.y()
			|| x == rectangle.maxX()
			|| y == rectangle.maxY();
	}

	public boolean contains(BasicCell cell) {
		return contains(cell.x, cell.y);
	}

	@Override
	public Iterator<Cell> iterator() {
		return new BasicBoundedCells(rectangle).without(
			new BasicBoundedCells(rectangle.shrink(1))
		).iterator();
	}


	/**
	 * Finds distance from line to rectangle's nearest border parallel to that line
	 */
	public int distanceToLine(BasicCell start, BasicCell end) {
		Orientation dir;
		if (start.x() == end.x()) {
			dir = Orientation.VERTICAL;
		} else if (start.y() == end.y()) {
			dir = Orientation.HORIZONTAL;
		} else {
			throw new Error(start + " and " + end + " are not on the same line");
		}
		if (dir.isVertical() && start.x() >= rectangle.x() && start.x() <= rectangle.maxX()) {
			throw new Error("Vertical line inside rectangle");
		} else if (dir.isHorizontal() && start.y() >= rectangle.y() && start.y() <= rectangle.maxY()) {
			throw new Error("Horizontal line inside rectangle");
		}
		if (dir.isVertical()) {
			return start.x() > rectangle.x() ? start.x() - rectangle.maxX() : rectangle.x() - start.x();
		} else {
			return start.y() > rectangle.y() ? start.y() - rectangle.maxY() : rectangle.y() - start.y();
		}
	}

	public BasicCell getMiddleOfSide(CardinalDirection side) {
		switch (side) {
			case N:
				return new BasicCell(rectangle.x() + rectangle.width() / 2, rectangle.y());
			case E:
				return new BasicCell(rectangle.x() + rectangle.width() - 1, rectangle.y() + rectangle.height() / 2);
			case S:
				return new BasicCell(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() - 1);
			case W:
				return new BasicCell(rectangle.x(), rectangle.y() + rectangle.height() / 2);
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Get cell on border.
	 *
	 * @param side
	 * 	Which border;
	 * @param endOfSide
	 * 	SideTest Determines one of the ends of border;
	 * @param depth
	 * 	How far is the cell from the end of the border. 0 is the first cell near end of border. Depth may be even
	 * 	more than width or height, so the cell will be outside the rectangle.
	 */
	public BasicCell getCellFromSide(CardinalDirection side, CardinalDirection endOfSide, int depth) {
		switch (side) {
			case N:
				switch (endOfSide) {
					case E:
						return new BasicCell(
							rectangle.maxX() - depth,
							rectangle.y()
						);
					case W:
						return new BasicCell(
							rectangle.x() + depth,
							rectangle.y()
						);
					default:
						throw new Error(
							"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")"
						);
				}
			case E:
				switch (endOfSide) {
					case N:
						return new BasicCell(
							rectangle.maxX(),
							rectangle.y() + depth
						);
					case S:
						return new BasicCell(
							rectangle.maxX(),
							rectangle.maxY() - depth
						);
					default:
						throw new Error(
							"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			case S:
				switch (endOfSide) {
					case E:
						return new BasicCell(
							rectangle.maxX() - depth,
							rectangle.maxY());
					case W:
						return new BasicCell(
							rectangle.x() + depth,
							rectangle.maxY()
						);
					default:
						throw new Error(
							"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			case W:
				switch (endOfSide) {
					case N:
						return new BasicCell(
							rectangle.x(),
							rectangle.y() + depth
						);
					case S:
						return new BasicCell(
							rectangle.x(),
							rectangle.maxY() - depth
						);
					default:
						throw new Error(
							"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			default:
				throw new Error("Incorrect side " + side.toInt());
		}
	}
}
