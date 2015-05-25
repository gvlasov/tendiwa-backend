package org.tendiwa.graphs.graphs2d;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.tendiwa.geometry.*;

import java.util.List;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle2D;

public final class BasicSplittableCycle2DTest {
	@Test
	public void can_split_edges() {
		Rectangle2D polygon = rectangle2D(50, 50);
		List<Segment2D> segments = polygon.toSegments();
		SplittableGraph2D graph = new BasicSplittableCycle2D(polygon);
		Segment2D topSegment = segments.get(0);
		graph.integrateCutSegment(
			new SplitSegment2D(
				topSegment,
				topSegment.middle()
			)
		);
		Segment2D leftSegment = segments.get(1);
		graph.integrateCutSegment(
			new ShreddedSegment2D(
				leftSegment,
				ImmutableList.of(
					leftSegment.pointAt(10),
					leftSegment.pointAt(20),
					leftSegment.pointAt(30)
				)
			)
		);
		assertEquals(
			4 + 1 + 3,
			graph.edgeSet().size()
		);
	}

	@Test
	public void knows_added_vertices_as_polygon() {
		Rectangle2D rectange = rectangle2D(10, 10);
		List<Segment2D> segments = rectange.toSegments();
		BasicSplittableCycle2D graph = new BasicSplittableCycle2D(rectange);
		Segment2D topSegment = segments.get(0);
		graph.integrateCutSegment(
			new SplitSegment2D(
				topSegment,
				topSegment.middle()
			)
		);
		Polygon polygon = graph;
		assertEquals(
			4+1,
			polygon.size()
		);
	}

}