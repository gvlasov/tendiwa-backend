package org.tendiwa.geometry;

import java.util.ArrayList;

public abstract class ConstructedPolygon extends ArrayList<Point2D> implements Polygon {
	protected ConstructedPolygon(int expectedSize) {
		super(expectedSize);
	}
}
