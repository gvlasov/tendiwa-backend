package tests;

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;

import org.junit.Test;

import tendiwa.core.meta.Side;
import tendiwa.geometry.EnhancedRectangle;

public class RectangleSystemTest {

	
	@Test
	public void testGrowRectangle() {
		assertEquals(
				EnhancedRectangle.growFromPoint(0, 0, Side.NE, 4, 7),
				new Rectangle(0, -7, 4, 7)
				);
		assertEquals(
				EnhancedRectangle.growFromPoint(0, 0, Side.NW, 2, 1),
				new Rectangle(-2, -1, 2, 1)
				);
		assertEquals(
				EnhancedRectangle.growFromPoint(0, 0, Side.SW, 2, 3),
				new Rectangle(-2, 0, 2, 3)
				);
		assertEquals(
				EnhancedRectangle.growFromPoint(0, 0, Side.SE, 9, 3),
				new Rectangle(0, 0, 9, 3)
				);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testWrongGrowRectangleE() {
		EnhancedRectangle.growFromPoint(0, 0, Side.E, 1, 1);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testWrongGrowRectangleW() {
		EnhancedRectangle.growFromPoint(0, 0, Side.S, 1, 1);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testWrongGrowRectangleN() {
		EnhancedRectangle.growFromPoint(0, 0, Side.W, 1, 1);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testWrongGrowRectangleS() {
		EnhancedRectangle.growFromPoint(0, 0, Side.N, 1, 1);
	}
}
