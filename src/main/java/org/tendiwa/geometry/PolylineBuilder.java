package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.*;

public final class PolylineBuilder {
	private final ImmutableList.Builder<Point2D> points;

	public PolylineBuilder() {
		this.points = ImmutableList.builder();
	}

	public void add(Point2D point) {
		points.add(point);
	}

	public void addAll(Collection<? extends Point2D> points) {
		this.points.addAll(points);
	}

	public Polyline build() {
		return new BasicPolyline(points.build());
	}
}
