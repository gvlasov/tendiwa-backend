package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Rectangle;

public interface RectangleWithNeighbors {
	Rectangle mainRectangle();

	ImmutableList<Rectangle> neighbors();

	Iterable<Rectangle> allRectangles();
}
