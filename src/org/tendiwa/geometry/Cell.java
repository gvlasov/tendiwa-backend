package org.tendiwa.geometry;

import org.tendiwa.core.Direction;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.CellPosition;

/**
 * Cell introduces several useful methods over Point class, as well as introduces a new concept of <b>dynamic
 * coordinate</b> and <b>static coordinate</b>. They are the same as x-coordinate and y-coordinate.
 * <p/>
 * X-coordinate is a horizontal dynamic coordinate, and it is a vertical static coordinate.
 * <p/>
 * On the contrary,  y-coordinate is a vertical dynamic coordinate and a horizontal static coordinate.
 * <p/>
 * Think of it the following way: if you take a horizontal line consisting of points, each point will have the same
 * y-coordinate (hence y is horizontal static) and different x coordinate (so x is horizontal dynamic)
 */
public class Cell implements CellPosition {
private final int x;
private final int y;

public Cell(int x, int y) {
	this.x = x;
	this.y = y;
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
 *         Orientation#HORIZONTAL}
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
 *         Orientation#VERTICAL}
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
}
