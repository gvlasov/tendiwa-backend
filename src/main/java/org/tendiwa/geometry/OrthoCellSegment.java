package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.core.meta.Range;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface OrthoCellSegment extends BoundedCellSet, Range {
	int getX();

	int getY();

	int length();

	Orientation orientation();

	default int min() {
		switch (orientation()) {
			case HORIZONTAL:
				return getX();
			case VERTICAL:
				return getY();
			default:
				throw new TwoDimensionalWorldConstraintViolation();
		}
	}

	default int max() {
		switch (orientation()) {
			case HORIZONTAL:
				return getX() + length() - 1;
			case VERTICAL:
				return getY() + length() - 1;
			default:
				throw new TwoDimensionalWorldConstraintViolation();
		}
	}

	default Rectangle getBounds() {
		return new BasicRectangle(
			getX(),
			getY(),
			orientation().isHorizontal() ? length() : 1,
			orientation().isVertical() ? length() : 1
		);
	}

	default boolean contains(int x, int y) {
		if (orientation().isHorizontal()) {
			return y == getY() && contains(getX());
		} else {
			return x == getX() && contains(getY());
		}
	}

	default Cell getEndPoint(CardinalDirection direction) {
		return new BasicCell(
			getX() + (orientation().isHorizontal() ? length() : 0),
			getY() + (orientation().isVertical() ? length() : 0)
		);
	}

	default int getStaticCoord() {
		return orientation().isHorizontal() ? getY() : getX();
	}

	/**
	 * Splits a segment using another segment. Creates one, two or zero new segments. </p> <p> Generally there are
	 * three
	 * cases of splitting: <ol> <li>If splitter segment covers part of the initial segment, then the answer will be one
	 * or
	 * two new segments.</li> <li>If splitter segment covers the whole initial segment, then the answer will be [null,
	 * null]</li> <li>If splitter segment and the initial segment don't intersect at all, then the answer will be
	 * [initialSegment, null]</li> </ol> <p> For example: </p> <ul> <li>------------ is the initial segment;</li>
	 * <li>+++ is
	 * an argument segment 4 cells long;</li> <li>-- and ------ are resulting segments</li> </ul> <p/>
	 * <pre>
	 * ------------
	 *
	 * --++++------
	 *
	 * --    ------
	 * </pre>
	 * <p> Another example, the same designation. Here the splitter segment is not fully inside the initial segment, so
	 * it
	 * removes only a part of the initial segment </p>
	 * <p>
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
	// TODO: Extract this method into a separate class
	default OrthoCellSegment[] splitWithSegment(int splitterStartCoord, int splitterLength) {
		OrthoCellSegment s1 = null, s2 = null;
		if (orientation().isHorizontal()) {
			// If splitting segment doesn't intersect with this segment, return
			// this segment
			if (splitterStartCoord > getX() + length() - 1 || splitterStartCoord + splitterLength < getX()) {
				return new OrthoCellSegment[]{
					this, null
				};
			}
			// A Segment before the splitting segment
			if (getX() < splitterStartCoord && splitterStartCoord < getX() + length()) {
				s1 = new BasicOrthoCellSegment(getX(), getY(), splitterStartCoord - getX(), orientation());
			}
			// A Segment after the splitting segment
			if (getX() + length() > splitterStartCoord + splitterLength && splitterStartCoord + splitterLength >
				getX()) {
				s2 = new BasicOrthoCellSegment(
					splitterStartCoord + splitterLength,
					getY(),
					length() - splitterLength - splitterStartCoord + getX(),
					orientation());
			}
			// If none of ifs are true, s1 and s2 remain null
		} else { // if (direction == DirectionToBERemoved.H)
			// If splitting segment doesn't intersect with this segment, return
			// this
			// segment
			if (splitterStartCoord > getY() + length() - 1 || splitterStartCoord + splitterLength < getY()) {
				return new OrthoCellSegment[]{
					this, null
				};
			}
			// A Segment before the splitting segment
			if (getY() < splitterStartCoord && splitterStartCoord < getY() + length()) {
				s1 = new BasicOrthoCellSegment(getX(), getY(), splitterStartCoord - getY(), orientation());
			}
			// A Segment after the splitting segment
			if (getY() + length() > splitterStartCoord + splitterLength && splitterStartCoord + splitterLength >
				getY()) {
				s2 = new BasicOrthoCellSegment(
					getX(),
					splitterStartCoord + splitterLength,
					length() - splitterLength - splitterStartCoord + getY(),
					orientation());
			}
			// If none of ifs are true, s1 and s2 remain null
		}
		return new OrthoCellSegment[]{
			s1, s2
		};
	}

	default Iterator<Cell> iterator() {
		int size = length();
		List<Cell> cells = new ArrayList<>(size);
		boolean horizontal = orientation().isHorizontal();
		for (int i = 0; i < size; i++) {
			cells.set(
				i,
				new BasicCell(
					getX() + (horizontal ? i : 0),
					getY() + (horizontal ? 0 : i)
				)
			);
		}
		return new IteratorWithoutRemove<>(cells.iterator());
	}
}
