package org.tendiwa.geometry;

/**
 * An immutable line
 */
public class Line2D {
public final Point2D start;
public final Point2D end;

public Line2D(Point2D start, Point2D end) {
	this.start = start;
	this.end = end;
}

@Override
public String toString() {
	return "Line2D{" +
		"start=" + start +
		", end=" + end +
		'}';
}
}
