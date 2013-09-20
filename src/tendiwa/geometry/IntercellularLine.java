package tendiwa.geometry;

import java.awt.*;

public class IntercellularLine {
	final int constantCoord;
	final Orientation orientation;

	/**
	 * Defines an infinite line that goes between two rows or columns of cells.
	 * Because line is infinite and may only have one of two directions, it is
	 * only defined by direction and one coordinate.
	 * 
	 * @param direction
	 *            Horizontal or vertical.
	 * @param constantCoord
	 *            If direction is vertical, then constant coord is the
	 *            coordinate of cells to the right of the line. If direction is
	 *            horizontal, then coord is the coordinate of cells to the
	 *            bottom of the line. That is, minimum possible coord for a line
	 *            is 0, and maximum possible coord (if the line is inside some
	 *            rectangle) is width or height of the area (instead of width-1
	 *            | height-1 for the coordinate of the rightmost/bottommost cell
	 *            in a rectangle).
	 */
	public IntercellularLine(Orientation direction, int constantCoord) {
		if (direction == null) {
			throw new NullPointerException();
		}
		this.orientation = direction;
		this.constantCoord = constantCoord;
	}
	public boolean isPerpendicular(IntercellularLine line2) {
		if (line2.orientation != orientation) {
			return true;
		}
		return false;
	}
	public int getStaticCoordFromSide(CardinalDirection side) {
		if (orientation.isVertical()) {
			switch (side) {
				case W:
					return constantCoord - 1;
				case E:
					return constantCoord;
				default:
					throw new IllegalArgumentException(
						"This is a vertical line, so only sides N and S are allowed");
			}
		} else {
			switch (side) {
				case N:
					return constantCoord - 1;
				case S:
					return constantCoord;
				default:
					throw new IllegalArgumentException(
						"This is a vertical line, so only sides N and S are allowed");
			}
		}
	}
	public static IntercellularLinesIntersection intersectionOf(IntercellularLine line1, IntercellularLine line2) {
		return new IntercellularLinesIntersection(line1, line2);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + constantCoord;
		result = prime * result + ((orientation == null) ? 0 : orientation
			.hashCode());
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
		IntercellularLine other = (IntercellularLine) obj;
		if (constantCoord != other.constantCoord)
			return false;
		if (orientation != other.orientation)
			return false;
		return true;
	}
	public boolean isParallel(IntercellularLine line) {
		return line.orientation == orientation;
	}
	public int distanceTo(IntercellularLine line) {
		if (!isParallel(line)) {
			throw new IllegalArgumentException(
				"Distance can be calculated only for parallel lines");
		}
		return Math.abs(constantCoord - line.constantCoord);
	}
	boolean hasPointFromSide(Point point, CardinalDirection direction) {
		assert point != null;
		assert direction != null;
		assert direction.isVertical() != orientation.isVertical();
		switch (direction) {
			case N:
				return point.y <= getStaticCoordFromSide(Directions.N);
			case E:
				return point.x >= getStaticCoordFromSide(Directions.E);
			case S:
				return point.y >= getStaticCoordFromSide(Directions.S);
			case W:
				return point.x <= getStaticCoordFromSide(Directions.W);
			default:
				return false;
		}
	}

}
