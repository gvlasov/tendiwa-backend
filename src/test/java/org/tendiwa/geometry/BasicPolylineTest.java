package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public final class BasicPolylineTest {
	@Test
	public void points_can_be_accessed_by_indices() {
		Polyline polyline = new BasicPolyline(
			point2D(0, 0),
			point2D(10, 0),
			point2D(10, 10)
		);
		assertEquals(
			point2D(0, 0),
			polyline.get(0)
		);
		assertEquals(
			point2D(10, 0),
			polyline.get(1)
		);
		assertEquals(
			point2D(10, 10),
			polyline.get(2)
		);
	}

	@Test
	public void can_be_turned_into_segments() {
		List<Segment2D> segments = new BasicPolyline(
			point2D(0, 0),
			point2D(10, 0),
			point2D(10, 10)
		).toSegments();
		assertEquals(
			2,
			segments.size()
		);
		assertEquals(
			segment2D(point2D(0, 0), point2D(10, 0)),
			segments.get(0)
		);
		assertEquals(
			segment2D(point2D(10, 0), point2D(10, 10)),
			segments.get(1)
		);
	}

}