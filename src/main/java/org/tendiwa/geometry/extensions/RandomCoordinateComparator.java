package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.Point2D;

import java.util.Comparator;
import java.util.Random;

public final class RandomCoordinateComparator implements Comparator<Point2D> {
	private final boolean coordinate;

	public RandomCoordinateComparator(Random random) {
		coordinate = random.nextBoolean();
	}

	@Override
	public int compare(Point2D o1, Point2D o2) {
		return (int) Math.signum(getCoordinate(o1) - getCoordinate(o2));
	}

	private double getCoordinate(Point2D o2) {
		return coordinate ? o2.x() : o2.y();
	}
}
