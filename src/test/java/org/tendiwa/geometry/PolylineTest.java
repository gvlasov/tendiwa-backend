package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public final class PolylineTest {

	@Test
	public void canBeSplit() {
		Polyline polyline = new BasicPolyline(
			ImmutableList.of(
				point2D(0, 0),
				point2D(20, 20),
				point2D(30, 34),
				point2D(70, 16),
				point2D(10, 55)
			)
		);
		ImmutableSet<Point2D> splitPoints = ImmutableSet.of(
			point2D(20, 20),
			point2D(70, 16)
		);
		ImmutableSet<Polyline> polylines = polyline.splitAtPoints(
			splitPoints
		);
		assertEquals(
			splitPoints.size() + 1,
			polylines.size()
		);
		assertEquals(
			polyline.size() + splitPoints.size(),
			polylines.stream()
				.mapToInt(Collection::size)
				.sum()
		);
	}

	@Test
	public void splittingAtStartDoesNothing() {
		Point2D start = point2D(0, 0);
		ImmutableSet<Polyline> polylines = new BasicPolyline(
			ImmutableList.of(
				start,
				point2D(10, 20),
				point2D(30, 40),
				point2D(50, 10)
			)
		).splitAtPoints(ImmutableSet.of(start));
		assertEquals(
			1,
			polylines.size()
		);
	}

	@Test
	public void splittingAtEndDoesNothing() {
		Point2D end = point2D(50, 10);
		ImmutableSet<Polyline> polylines = new BasicPolyline(
			ImmutableList.of(
				point2D(0, 0),
				point2D(10, 20),
				point2D(30, 40),
				end
			)
		).splitAtPoints(ImmutableSet.of(end));
		assertEquals(
			1,
			polylines.size()
		);
	}
}