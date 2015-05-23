package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public final class BasicRectangle2DTest {
	@Test
	public void integerBoundsArePlus1InDimensions() {
		final Rectangle2D rectangle = new BasicRectangle2D(0, 0, 2, 2);
		assertEquals(
			3 * 3,
			rectangle.integerBounds().area()
		);
	}
}