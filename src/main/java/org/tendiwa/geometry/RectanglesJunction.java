package org.tendiwa.geometry;

import org.tendiwa.core.Orientation;

/**
 * This class represents a junction between two rectangles. It's a rectangle that overlaps two rectangles with a
 * single {@link BasicOrthoCellSegment} each. Junction consists of those two segments and space between
 * rectangles. You can think of junction as of a rectangular bridge with particular width between two rectangles.
 */
public class RectanglesJunction {
	final Rectangle r1;
	final Rectangle r2;
	private final Orientation orientation;
	private final int coordinate;
	private final int width;

	public RectanglesJunction(Orientation orientation, int coordinate, int width, Rectangle r1, Rectangle r2) {
		this.orientation = orientation;
		this.coordinate = coordinate;
		this.width = width;
		this.r1 = r1;
		this.r2 = r2;
	}

	OrthoCellSegment getSegmentOnRectangle(Rectangle r) {
		throw new UnsupportedOperationException();
	}

	Rectangle getRectangleBetweenRectangles() {
		throw new UnsupportedOperationException();
	}
}
