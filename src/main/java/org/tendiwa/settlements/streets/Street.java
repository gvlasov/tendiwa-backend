package org.tendiwa.settlements.streets;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Point2D;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

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
