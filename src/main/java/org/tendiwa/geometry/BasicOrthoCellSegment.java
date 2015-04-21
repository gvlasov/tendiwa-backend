package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Cell;

import java.util.Iterator;
import java.util.Objects;

/**
 * A Segment is a horizontal or vertical line starting at point {x;y} %length% cells long. If direction of segment is
 * {@link org.tendiwa.core.Orientation#VERTICAL}, then {x;y} is its top cell. If direction is {@link
 * org.tendiwa.core.Orientation#HORIZONTAL}, then {x;y} is its leftmost cell.
 */
public class BasicOrthoCellSegment implements OrthoCellSegment {
	private final Orientation orientation;
	private final int x;
	private final int y;
	private final int length;

	public BasicOrthoCellSegment(int x, int y, int length, Orientation orientation) {
		if (length < 1) {
			throw new IllegalArgumentException(
				"Length must be >= 1 (length equals " + length + ")"
			);
		}
		Objects.requireNonNull(orientation);
		this.x = x;
		this.y = y;
		this.length = length;
		this.orientation = orientation;
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
		return length;
	}

	@Override
	public Orientation orientation() {
		return orientation;
	}

	@Override
	public String toString() {
		return x + ":" + y + ", " + length;
	}

	@Override
	public int min() {
		if (orientation.isVertical()) {
			return y;
		} else {
			return x;
		}
	}

	@Override
	public int max() {
		if (orientation.isVertical()) {
			return y + length - 1;
		} else {
			return x + length - 1;
		}
	}

	@Override
	public BasicCell getEndPoint(CardinalDirection direction) {
		if (orientation == Orientation.VERTICAL) {
			if (direction == Directions.N) {
				return new BasicCell(x, y);
			} else if (direction == Directions.S) {
				return new BasicCell(x, y + length - 1);
			}
		} else {
			if (direction == Directions.W) {
				return new BasicCell(x, y);
			} else if (direction == Directions.E) {
				return new BasicCell(x + length - 1, y);
			}
		}
		throw new IllegalArgumentException(
			"Can't get " + direction + " end point of a " + orientation + " segment"
		);
	}

	@Override
	public Iterator<Cell> iterator() {
		return new Iterator<Cell>() {
			final int staticCoord = orientation.isVertical() ? x : y;
			int dynamicCoord = orientation.isVertical() ? y : x;
			final int endDynamicCoord = dynamicCoord + length - 1;

			@Override
			public boolean hasNext() {
				return dynamicCoord <= endDynamicCoord;
			}

			@Override
			public BasicCell next() {
				return Cells.fromStaticAndDynamic(
					staticCoord,
					dynamicCoord++,
					orientation
				);
			}

			@Override
			@Deprecated
			public void remove() {
				throw new UnsupportedOperationException("Should not be implemented");
			}

		};
	}

	@Override
	public int getStaticCoord() {
		if (orientation.isVertical()) {
			return x;
		} else {
			return y;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + ((orientation == null) ? 0 : orientation
			.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicOrthoCellSegment other = (BasicOrthoCellSegment) obj;
		if (length != other.length)
			return false;
		if (orientation != other.orientation)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}