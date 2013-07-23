package tendiwa.geometry;

import java.awt.Point;

/**
 * A Segment is a horizontal or vertical line starting at point {x;y} %length%
 * cells long. If direction of segment is {@link DirectionToBERemoved#V}, then
 * {x;y} is its top cell. If direction is {@link DirectionToBERemoved#H}, then
 * {x;y} is its leftmost cell.
 */
public class Segment {
	public int x;
	public int y;
	public int length;
	final Orientation orientation;

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
	 * <p>
	 * Splits a segment using another segment. Creates one, two or zero new
	 * segments.
	 * </p>
	 * <p>
	 * Generally there are three cases of splitting:
	 * </p>
	 * <ol>
	 * <li>If splitter segment covers part of the initial segment, then the
	 * answer will be one or two new segments.</li>
	 * <li>If splitter segment covers the whole initial segment, then the answer
	 * will be [null, null]</li>
	 * <li>If splitter segment and the initial segment don't intersect at all,
	 * then the answer will be [initialSegment, null]</li>
	 * </ol>
	 * <p>
	 * For example:
	 * </p>
	 * <ul>
	 * <li>------------ is the initial segment;</li>
	 * <li>+++ is an argument segment 4 cells long;</li>
	 * <li>-- and ------ are resulting segments</li>
	 * </ul>
	 * 
	 * <pre>
	 * ------------
	 * 
	 * --++++------
	 * 
	 * --    ------
	 * </pre>
	 * <p>
	 * Another example, the same designation. Here the splitter segment is not
	 * fully inside the initial segment, so it removes only a part of the
	 * initial segment
	 * </p>
	 * 
	 * <pre>
	 * ------------
	 * 
	 * ---------+++++
	 * 
	 * ---------
	 * </pre>
	 * 
	 * 
	 * @return Two Segments in an array, or one Segment and null, or two nulls.
	 */
	public Segment[] splitWithSegment(int splitterStartCoord, int splitterLength) {
		Segment s1 = null, s2 = null;
		if (orientation.isHorizontal()) {
			// If splitting segment doesn't intersect with this segment, return
			// this segment
			if (splitterStartCoord > x + length - 1 || splitterStartCoord + splitterLength < x) {
				return new Segment[] { this, null };
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
				return new Segment[] { this, null };
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
		return new Segment[] { s1, s2 };
	}
	public Segment clone() {
		return new Segment(x, y, length, orientation);
	}
	/**
	 * Changes the x coordinate of Segment's start by dx cells.
	 * 
	 * @param dx
	 *            Moved start of Segment to east of positive, or to west if
	 *            negative, by that amount of cells.
	 */
	public void changeX(int dx) {
		x += dx;
	}
	/**
	 * Changes the y coordinate of Segment's start by dy cells.
	 * 
	 * @param dx
	 *            Moved start of Segment to south of positive, or to north if
	 *            negative, by that amount of cells.
	 */
	public void changeY(int dy) {
		y += dy;
	}
	/**
	 * Changes the length of Segment's by certain amount of cells. Lengthens the
	 * Segment of positive, or shortens the Segment of negative.
	 * 
	 * @param dlength
	 */
	public void changeLength(int dlength) {
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
	public Point getEndPoint(CardinalDirection direction) {
		if (orientation == Orientation.VERTICAL) {
			if (direction == Directions.N) {
				return new Point(x, y);
			} else if (direction == Directions.S) {
				return new Point(x, y + length - 1);
			}
		} else {
			if (direction == Directions.W) {
				return new Point(x, y);
			} else if (direction == Directions.E) {
				return new Point(x + length - 1, y);
			}
		}
		throw new IllegalArgumentException(
			"Can't get " + direction + " end point of a " + orientation + " segment");
	}
}