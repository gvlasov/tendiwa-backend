package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.BasicPolygon;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon_Wr;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;

public final class GraphCyclePolygon extends Polygon_Wr {
	public GraphCyclePolygon(MinimalCycle<Point2D, Segment2D> cycle) {
		super(
			new BasicPolygon(
				ImmutableList.copyOf(cycle.asVertices())
			)
		);
	}
}
