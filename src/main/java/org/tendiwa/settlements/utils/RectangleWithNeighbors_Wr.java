package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Rectangle;

public abstract class RectangleWithNeighbors_Wr implements RectangleWithNeighbors {
	private final RectangleWithNeighbors rectangleWithNeighbors;

	@Override
	public Rectangle mainRectangle() {
		return rectangleWithNeighbors.mainRectangle();
	}

	@Override
	public ImmutableList<Rectangle> neighbors() {
		return rectangleWithNeighbors.neighbors();
	}

	@Override
	public Iterable<Rectangle> allRectangles() {
		return rectangleWithNeighbors.allRectangles();
	}

	public RectangleWithNeighbors_Wr(RectangleWithNeighbors rectangleWithNeighbors) {
		this.rectangleWithNeighbors = rectangleWithNeighbors;
	}
}