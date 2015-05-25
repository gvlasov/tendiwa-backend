package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

public abstract class ConstructedPolygon extends ArrayList<Point2D> implements Polygon {
	@Override
	public ImmutableList<Point2D> toImmutableList() {
		return ImmutableList.copyOf(this);
	}

	protected ConstructedPolygon(int expectedSize) {
		super(expectedSize);
	}
}
