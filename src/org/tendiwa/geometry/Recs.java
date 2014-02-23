package org.tendiwa.geometry;

import org.tendiwa.core.OrdinalDirection;
import org.tendiwa.core.meta.Coordinate;

import java.util.Collection;

public class Recs {
/**
 * A more convenient method for creating rectangles. Takes a point, places another point from ordinal direction from the
 * initial point.
 *
 * @param x
 * 	Initial point
 * @param y
 * 	Initial point
 * @param side
 * 	Location of the second point relatively from the initial point.
 * @param width
 * 	How far is the second point from the initial point on x-axis.
 * @param height
 * 	How far is the second point from the initial point on y-axis.
 * @return New rectangle that grown from a point in certain direciton.
 */
public static Rectangle growFromPoint(int x, int y, OrdinalDirection side, int width, int height) {
	switch (side) {
		case SE:
			return new Rectangle(x, y, width, height);
		case NE:
			return new Rectangle(x, y - height + 1, width, height);
		case NW:
			return new Rectangle(
				x - width + 1,
				y - height + 1,
				width,
				height);
		case SW:
			return new Rectangle(x - width + 1, y, width, height);
		default:
			throw new IllegalArgumentException();
	}
}

/**
 * Returns rectangle defined by two corner points
 */
public static Rectangle getRectangleFromTwoCorners(Coordinate c1, Coordinate c2) {
	int startX = Math.min(c1.x, c2.x);
	int startY = Math.min(c1.y, c2.y);
	int recWidth = Math.max(c1.x, c2.x) - startX + 1;
	int recHeight = Math.max(c1.y, c2.y) - startY + 1;
	return new Rectangle(startX, startY, recWidth, recHeight);
}

/**
 * <p> Creates a new Rectangle defined by its minimum and maximum coordinates. </p>
 *
 * @param xMin
 * 	Least coordinate by x-axis.
 * @param yMin
 * 	Least coordinate by y-axis.
 * @param xMax
 * 	Greatest coordinate by x-axis.
 * @param yMax
 * 	Greatest coordinate by y-axis.
 * @return A new Rectangle bounded by these two points (containing both of them inside).
 */
public static Rectangle rectangleByMinAndMaxCoords(int xMin, int yMin, int xMax, int yMax) {
	if (xMin > xMax) {
		throw new IllegalArgumentException("xMin can't be > xMax");
	}
	if (yMin > yMax) {
		throw new IllegalArgumentException("yMin can't be > yMax");
	}
	return new Rectangle(
		xMin,
		yMin,
		xMax - xMin + 1,
		yMax - yMin + 1);
}

/**
 * Returns the minimum rectangle containing all the given points inside it.
 *
 * @param points
 * @return
 */
public static Rectangle rectangleContainingAllPonts(Collection<Cell> points) {
	int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
	for (Cell point : points) {
		if (point.getX() < minX) {
			minX = point.getX();
		}
		if (point.getX() > maxX) {
			maxX = point.getX();
		}
		if (point.getY() < minY) {
			minY = point.getY();
		}
		if (point.getY() > maxY) {
			maxY = point.getY();
		}
	}
	return new Rectangle(
		minX,
		minY,
		maxX - minX + 1,
		maxY - minY + 1);
}

/**
 * Creates a new rectangle whose center is the given cell, with given width and height. If the rectangle created has
 * even width/height, the exact center coordinate will be randomized between two possible coordinates.
 *
 * @param cell
 * @param width
 * @param height
 * @return
 */
public static Rectangle rectangleByCenterPoint(Cell cell, int width, int height) {
	return new Rectangle(
		cell.getX() - width / 2,
		cell.getY() - height / 2,
		width,
		height);
}

/**
 * Grows a new Rectangle from a point where two {@link org.tendiwa.geometry.IntercellularLine}s intersect. An intersection of two such lines
 * divides the plane in 4 quadrants, and the quadrant where the rectangle will be is defined by DirectionOldSide
 * argument.
 *
 * @param line1
 * 	Must be perpendicular to line2.
 * @param line2
 * 	Must be perpendicular to line1.
 * @param side
 * 	Must be ordinal.
 * @param width
 * 	Width of the resulting rectangle.
 * @param height
 * 	Height of the resulting rectangle.
 * @return
 * @see {@link #growFromPoint(int, int, org.tendiwa.core.OrdinalDirection, int, int)}
 */
public static Rectangle growFromIntersection(IntercellularLine line1, IntercellularLine line2, OrdinalDirection side, int width, int height) {
	if (!line1.isPerpendicular(line2)) {
		throw new IllegalArgumentException(
			"Two lines must be perpendicular");
	}
	IntercellularLinesIntersection intersection = IntercellularLine
		.intersectionOf(line1, line2);
	return growFromIntersection(intersection, side, width, height);

}

/**
 * Grows a new Rectangle from a point where two {@link org.tendiwa.geometry.IntercellularLine}s intersect. An intersection of two such lines
 * divides the plane in 4 quadrants, and the quadrant where the rectangle will be is defined by DirectionOldSide
 * argument.
 *
 * @param intersection
 * @param side
 * 	Must be ordinal.
 * @param width
 * 	Width of the resulting rectangle.
 * @param height
 * 	Height of the resulting rectangle.
 * @return
 */
public static Rectangle growFromIntersection(IntercellularLinesIntersection intersection, OrdinalDirection side, int width, int height) {
	Cell point = intersection.getCornerPointOfQuarter(side);
	return growFromPoint(point.getX(), point.getY(), side, width, height);
}

/**
 * Creates a new {@link org.tendiwa.geometry.Rectangle} relative to an already existing {@link java.awt.Rectangle}.
 *
 * @param r
 * 	an already existing rectangle.
 * @param dx
 * 	how far will the new rectangle be shifted by x-axis from the original one.
 * @param dy
 * 	how far will the new rectangle be shifted by x-axis from the original one.
 * @return a new {@link org.tendiwa.geometry.Rectangle} with width and height equal to {@code r}'s.
 */
public static Rectangle rectangleMovedFromOriginal(Rectangle r, int dx, int dy) {
	if (r == null) {
		throw new NullPointerException();
	}
	return new Rectangle(
		r.getX() + dx,
		r.getY() + dy,
		r.getWidth(),
		r.getHeight()
	);

}
}