package org.tendiwa.settlements.streets;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Point2D;

public class Street {

	final ImmutableList<Point2D> points;
	private String localizationId;

	public Street(ImmutableList<Point2D> points, String localizationId) {
		this.points = points;
		this.localizationId = localizationId;
	}

	public ImmutableList<Point2D> getPoints() {
		return points;
	}
}
