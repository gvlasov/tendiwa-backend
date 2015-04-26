package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class RectangleWithNeighborsTest {
	@Test
	public void rectangleWithoutNeighborsIterations() {
		assertEquals(
			1,
			Iterables.size(
				new BasicRectangleWithNeighbors(
					rectangle(1, 2, 3, 4),
					ImmutableList.of()
				).allRectangles()
			)
		);
	}

	@Test
	public void rectangleWithOneNeighborIterations() {
		assertEquals(
			2,
			Iterables.size(
				new BasicRectangleWithNeighbors(
					rectangle(1, 2, 3, 4),
					ImmutableList.of(
						rectangle(2, 3, 4, 5)
					)
				).allRectangles()
			)
		);
	}

	@Test
	public void rectangleWithTwoNeighborsIterations() {
		assertEquals(
			3,
			Iterables.size(
				new BasicRectangleWithNeighbors(
					rectangle(1, 2, 3, 4),
					ImmutableList.of(
						rectangle(2, 3, 4, 5),
						rectangle(3, 4, 5, 6)
					)
				).allRectangles()
			)
		);
	}

	@Test
	public void rectangleWithTenNeighborsIterations() {
		assertEquals(
			11,
			Iterables.size(
				new BasicRectangleWithNeighbors(
					rectangle(1, 2, 3, 4),
					ImmutableList.of(
						rectangle(2, 3, 4, 5),
						rectangle(3, 4, 5, 6),
						rectangle(4, 3, 4, 5),
						rectangle(5, 3, 4, 5),
						rectangle(6, 3, 4, 5),
						rectangle(7, 3, 4, 5),
						rectangle(8, 3, 4, 5),
						rectangle(9, 3, 4, 5),
						rectangle(10, 3, 4, 5),
						rectangle(10, 3, 4, 5)
					)
				).allRectangles()
			)
		);
	}

}