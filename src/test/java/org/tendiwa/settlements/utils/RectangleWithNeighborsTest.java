package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.junit.Test;
import org.tendiwa.geometry.Rectangle;

import static org.junit.Assert.*;

public class RectangleWithNeighborsTest {
	@Test
	public void rectangleWithoutNeighborsIterations() {
		RectangleWithNeighbors rectangleWithNeighbors = new RectangleWithNeighbors(
			new Rectangle(1, 2, 3, 4),
			ImmutableList.of()
		);
		assertEquals(1, Iterables.size(rectangleWithNeighbors.allRectangles()));
	}

	@Test
	public void rectangleWithOneNeighborIterations() {
		RectangleWithNeighbors rectangleWithNeighbors = new RectangleWithNeighbors(
			new Rectangle(1, 2, 3, 4),
			ImmutableList.of(
				new Rectangle(2, 3, 4, 5)
			)
		);
		assertEquals(2, Iterables.size(rectangleWithNeighbors.allRectangles()));
	}

	@Test
	public void rectangleWithTwoNeighborsIterations() {
		RectangleWithNeighbors rectangleWithNeighbors = new RectangleWithNeighbors(
			new Rectangle(1, 2, 3, 4),
			ImmutableList.of(
				new Rectangle(2, 3, 4, 5),
				new Rectangle(3, 4, 5, 6)
			)
		);
		assertEquals(3, Iterables.size(rectangleWithNeighbors.allRectangles()));
	}

	@Test
	public void rectangleWithTenNeighborsIterations() {
		RectangleWithNeighbors rectangleWithNeighbors = new RectangleWithNeighbors(
			new Rectangle(1, 2, 3, 4),
			ImmutableList.of(
				new Rectangle(2, 3, 4, 5),
				new Rectangle(3, 4, 5, 6),
				new Rectangle(4, 3, 4, 5),
				new Rectangle(5, 3, 4, 5),
				new Rectangle(6, 3, 4, 5),
				new Rectangle(7, 3, 4, 5),
				new Rectangle(8, 3, 4, 5),
				new Rectangle(9, 3, 4, 5),
				new Rectangle(10, 3, 4, 5),
				new Rectangle(10, 3, 4, 5)
			)
		);
		assertEquals(11, Iterables.size(rectangleWithNeighbors.allRectangles()));
	}

}