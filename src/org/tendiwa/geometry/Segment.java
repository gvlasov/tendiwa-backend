package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Range;

import java.util.Iterator;

/**
 * A Segment is a horizontal or vertical line starting at point {x;y} %length% cells long. If direction of segment is
 * {@link org.tendiwa.core.Orientation#VERTICAL}, then {x;y} is its top cell. If direction is {@link org.tendiwa.core.Orientation#HORIZONTAL}, then {x;y}
 * is its leftmost cell.
 */
public class Segment implements Iterable<Cell> {
final Orientation orientation;
// TODO: Make Segment immutable
public int x;
public int y;
public int length;

public Segment(int x, int y, int length, Orientation orientation) {
	if (length < 1) {
		throw new IllegalArgumentException(
			"Length must be >= 1 (it is now " + length + ")");
	}
	if (orientation == null) {
		throw new NullPointerException();
	}
	this.x = x;
	this.y = y;
	this.length = length;
	this.orientation = orientation;
}

/**
 * <p> Splits a segment using another segment. Creates one, two or zero new segments. </p> <p> Generally there are
 * three cases of splitting: </p> <ol> <li>If splitter segment covers part of the initial segment, then the answer will
 * be one or two new segments.</li> <li>If splitter segment covers the whole initial segment, then the answer will be
 * [null, null]</li> <li>If splitter segment and the initial segment don't intersect at all, then the answer will be
 * [initialSegment, null]</li> </ol> <p> For example: </p> <ul> <li>------------ is the initial segment;</li> <li>+++
 * is an argument segment 4 cells long;</li> <li>-- and ------ are resulting segments</li> </ul>
 * <p/>
 * <pre>
 * ------------
 *
 * --++++------
 *
 * --    ------
 * </pre>
 * <p> Another example, the same designation. Here the splitter segment is not fully inside the initial segment, so it
 * removes only a part of the initial segment </p>
 * <p/>
 * <pre>
 * ------------
 *
 * ---------+++++
 *
 * ---------
 * </pre>
 *
 * @return Two Segments in an array, or one Segment and null, or two nulls.
 */
public Segment[] splitWithSegment(int splitterStartCoord, int splitterLength) {
	Segment s1 = null, s2 = null;
	if (orientation.isHorizontal()) {
		// If splitting segment doesn't intersect with this segment, return
		// this segment
		if (splitterStartCoord > x + length - 1 || splitterStartCoord + splitterLength < x) {
			return new Segment[]{
				this, null
			};
		}
		// A Segment before the splitting segment
		if (x < splitterStartCoord && splitterStartCoord < x + length) {
			s1 = new Segment(x, y, splitterStartCoord - x, orientation);
		}
		// A Segment after the splitting segment
		if (x + length > splitterStartCoord + splitterLength && splitterStartCoord + splitterLength > x) {
			s2 = new Segment(
				splitterStartCoord + splitterLength,
				y,
				length - splitterLength - splitterStartCoord + x,
				orientation);
		}
		// If none of ifs are true, s1 and s2 remain null
	} else { // if (direction == DirectionToBERemoved.H)
		// If splitting segment doesn't intersect with this segment, return
		// this
		// segment
		if (splitterStartCoord > y + length - 1 || splitterStartCoord + splitterLength < y) {
			return new Segment[]{
				this, null
			};
		}
		// A Segment before the splitting segment
		if (y < splitterStartCoord && splitterStartCoord < y + length) {
			s1 = new Segment(x, y, splitterStartCoord - y, orientation);
		}
		// A Segment after the splitting segment
		if (y + length > splitterStartCoord + splitterLength && splitterStartCoord + splitterLength > y) {
			s2 = new Segment(
				x,
				splitterStartCoord + splitterLength,
				length - splitterLength - splitterStartCoord + y,
				orientation);
		}
		// If none of ifs are true, s1 and s2 remain null
	}
	return new Segment[]{
		s1, s2
	};
}

public Segment clone() {
	return new Segment(x, y, length, orientation);
}

/**
 * Changes the x coordinate of Segment's start by dx cells.
 *
 * @param dx
 * 	Moved start of Segment to east of positive, or to west if negative, by that amount of cells.
 */
public void changeX(int dx) {
	x += dx;
}

/**
 * Changes the y coordinate of Segment's start by dy cells.
 *
 * @param dy
 * 	Moved start of Segment to south of positive, or to north if negative, by that amount of cells.
 */
public void changeY(int dy) {
	y += dy;
}

/**
 * Changes the length of Segment's by certain amount of cells. Lengthens the Segment of positive, or shortens the
 * Segment of negative.
 *
 * @param dlength
 * 	Amout of cells to change. If positive, it will increase length. If negative, it will decrease length.
 */
public void changeLength(int dlength) {
	if (length + dlength <= 0) {
		throw new IllegalArgumentException("Changing length " + length + " by " + dlength + " will set it to less than zero");
	}
	length += dlength;
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

public int getLength() {
	return length;
}

public Orientation getOrientation() {
	return orientation;
}

@Override
public String toString() {
	return "" + (orientation.isHorizontal() ? x : y) + "," + length;
}

public int getStartCoord() {
	if (orientation.isVertical()) {
		return y;
	} else {
		return x;
	}
}

public int getEndCoord() {
	if (orientation.isVertical()) {
		return y + length - 1;
	} else {
		return x + length - 1;
	}
}

public Cell getEndPoint(CardinalDirection direction) {
	if (orientation == Orientation.VERTICAL) {
		if (direction == Directions.N) {
			return new Cell(x, y);
		} else if (direction == Directions.S) {
			return new Cell(x, y + length - 1);
		}
	} else {
		if (direction == Directions.W) {
			return new Cell(x, y);
		} else if (direction == Directions.E) {
			return new Cell(x + length - 1, y);
		}
	}
	throw new IllegalArgumentException(
		"Can't get " + direction + " end point of a " + orientation + " segment");
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
		public Cell next() {
			return Cell.fromStaticAndDynamic(
				staticCoord,
				dynamicCoord++,
				orientation);
		}

		@Override
		@Deprecated
		public void remove() {
			throw new UnsupportedOperationException("Should not be implemented");
		}

	};
}

/**
 * Returns a new {@link org.tendiwa.core.meta.Range} object from this segment's start dynamic coord to its end dynamic coord.
 *
 * @return Range of this Segment's dynamic coordinates.
 * @see Segment#getStartCoord()
 * @see Segment#getEndCoord()
 */
public Range asRange() {
	return new Range(getStartCoord(), getEndCoord());
}

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
	Segment other = (Segment) obj;
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