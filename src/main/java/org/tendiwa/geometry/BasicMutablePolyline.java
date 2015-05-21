package org.tendiwa.geometry;

import java.util.ArrayList;

public class BasicMutablePolyline extends ArrayList<Point2D> implements MutablePolyline {

	public BasicMutablePolyline(int expectedSize) {
		super(expectedSize);
	}

	@Override
	public void addInFront(Point2D point) {
		super.add(point);
	}
}
