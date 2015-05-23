package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public final class Mutable2DCellSetTest {
	@Test
	public void excludesRectangle() {
		int width = 13;
		int height = 10;
		Rectangle rectangle = rectangle(width, height);
		MutableBoundedCellSet cells = new Mutable2DCellSet(rectangle);
		cells.excludeRectangle(rectangle.shrink(1));
		assertEquals(
			cells.toSet().size(),
			width * 2 + (height - 2) * 2
		);
	}

	@Test
	public void excludesAllCells() {
		int width = 10;
		int height = 17;
		Rectangle rectangle = rectangle(width, height);
		MutableBoundedCellSet cells = new Mutable2DCellSet(rectangle);
		cells.excludeRectangle(rectangle);
		assertEquals(
			cells.toSet().size(),
			0
		);
	}
}