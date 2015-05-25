package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public final class BasicPolygonTest {
	@Test
	public void toSegments() {
		assertEquals(
			4,
			new BasicPolygon(
				point2D(0, 0),
				point2D(10, 10),
				point2D(10, 20),
				point2D(5, 30)
			)
				.toSegments()
				.size()
		);
	}

	@Test
	public void points_can_be_accessed_by_indices() {
		Polygon polygon = new BasicPolygon(
			point2D(0, 0),
			point2D(0, 10),
			point2D(5, 0)
		);
		assertEquals(
			point2D(0, 0),
			polygon.get(0)
		);
		assertEquals(
			point2D(0, 10),
			polygon.get(1)
		);
		assertEquals(
			point2D(5, 0),
			polygon.get(2)
		);
	}

}