package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.lexeme.Localizable;

import java.util.List;

public class Street {
	private final List<Point2D> points;
	private Localizable streetName;

	public Street(List<Point2D> points, Localizable streetName) {
		this.points = points;
		this.streetName = streetName;
	}
}
