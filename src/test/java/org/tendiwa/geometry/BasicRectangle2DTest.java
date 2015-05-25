package org.tendiwa.geometry;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public final class BasicRectangle2DTest {
	@Test
	public void integer_bounds_are_plus_1_in_dimensions() {
		final Rectangle2D rectangle = new BasicRectangle2D(0, 0, 2, 2);
		assertEquals(
			3 * 3,
			rectangle.integerBounds().area()
		);
	}

	@Test
	public void segments_order_is_top_left_bottom_right() {
		Rectangle2D rectangle = new BasicRectangle2D(0, 0, 5, 5);
		List<Segment2D> segments = rectangle.toSegments();
		assertEquals(
			rectangle.nwCorner().segmentTo(rectangle.neCorner()),
			segments.get(0)
		);
		assertEquals(
			rectangle.neCorner().segmentTo(rectangle.seCorner()),
			segments.get(1)
		);
		assertEquals(
			rectangle.seCorner().segmentTo(rectangle.swCorner()),
			segments.get(2)
		);
		assertEquals(
			rectangle.swCorner().segmentTo(rectangle.nwCorner()),
			segments.get(3)
		);
	}
}