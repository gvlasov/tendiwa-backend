package org.tendiwa.graphs.graphs2d;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.*;

public class BasicOrientedPolygon extends BasicPolygon implements OrientedPolygon {

	private final ReverseEdges reverseEdges;
	private final boolean isClockwise;

	public BasicOrientedPolygon(Polygon polygon) {
		super(polygon.toImmutableList());
		this.isClockwise = isClockwise();
		this.reverseEdges = new ReverseEdges(polygon);
	}

	@Override
	public final boolean isClockwise(Segment2D edge) {
		return isClockwise ^ reverseEdges.isAgainstCycleDirection(edge);
	}
}
