package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.lexeme.Localizable;

import java.util.List;

public class Street {
	final List<Point2D> points;
	private String localizationId;

	public Street(List<Point2D> points, String localizationId) {
		this.points = points;
		this.localizationId = localizationId;
	}
}
