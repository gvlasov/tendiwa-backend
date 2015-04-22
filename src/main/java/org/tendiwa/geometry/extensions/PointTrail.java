package org.tendiwa.geometry.extensions;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.BasicPoint2D;
import org.tendiwa.geometry.Point2D;

import java.util.List;

public class PointTrail {

	private final ImmutableList.Builder<Point2D> builder;
	private Point2D last;

	public PointTrail(double x, double y) {
		builder = ImmutableList.builder();
		add(new BasicPoint2D(x, y));
	}

	private void add(Point2D point) {
		builder.add(point);
		last = point;
	}

	public PointTrail moveBy(double dx, double dy) {
		add(last.moveBy(dx, dy));
		return this;
	}

	public PointTrail moveByX(double dx) {
		add(last.moveBy(dx, 0));
		return this;
	}

	public PointTrail moveByY(double dy) {
		add(last.moveBy(0, dy));
		return this;
	}

	public List<Point2D> points() {
		return builder.build();
	}
}
