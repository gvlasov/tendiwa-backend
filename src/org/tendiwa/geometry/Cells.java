package org.tendiwa.geometry;

import org.tendiwa.core.Orientation;

/**
 * Static utility class for operations on cells' coordinates.
 */
public class Cells {
private Cells() {
	throw new UnsupportedOperationException("Private constructor of a utility class");
}

public static boolean isNear(int startX, int startY, int endX, int endY) {
	int ableX = Math.abs(startX - endX);
	int ableY = Math.abs(startY - endY);
	return (ableX == 1 && ableY == 0) || (ableY == 1 && ableX == 0) || (ableY == 1 && ableX == 1);
}

/**
 * Returns distance in cells between two points, rounded down to be integer (actual distance is of a type double).
 *
 * @param startX
 * 	X coordinate of point 1
 * @param startY
 * 	Y coordinate of point 1
 * @param endX
 * 	X coordinate of point 2
 * @param endY
 * 	Y coordinate of point 2
 * @return Distance between two points, rounded down.
 * @see #distanceDouble(int, int, int, int)
 */
public static int distanceInt(int startX, int startY, int endX, int endY) {
	return (int) Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
}

/**
 * Returns exact distance between two points.
 *
 * @param startX
 * 	X coordinate of point 1
 * @param startY
 * 	Y coordinate of point 1
 * @param endX
 * 	X coordinate of point 2
 * @param endY
 * 	Y coordinate of point 2
 * @return Exact distance between two points.
 * @see {@link #distanceInt(int, int, int, int)}  for inexact distance, rounded down to.
 */
public static double distanceDouble(int startX, int startY, int endX, int endY) {
	return Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
}

public static Cell fromStaticAndDynamic(int staticCoord, int dynamicCoord, Orientation orientation) {
	if (orientation.isVertical()) {
		return new Cell(staticCoord, dynamicCoord);
	} else {
		return new Cell(dynamicCoord, staticCoord);
	}
}
}