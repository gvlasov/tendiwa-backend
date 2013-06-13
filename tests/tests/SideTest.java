package tests;

import junit.framework.TestCase;

import org.junit.Test;

import tendiwa.core.meta.Side;

public class SideTest extends TestCase {

	@Test
	public void testD2Side() {
		System.out.println(Side.d2side(1, 4));
		assertEquals(Side.d2side(1, -4), Side.N);
		assertEquals(Side.d2side(-1, -4), Side.N);
		assertEquals(Side.d2side(-1, 4), Side.S);
		assertEquals(Side.d2side(1, 4), Side.S);
		assertEquals(Side.d2side(4, 1), Side.E);
		assertEquals(Side.d2side(4, -1), Side.E);
		assertEquals(Side.d2side(-4, 1), Side.W);
		assertEquals(Side.d2side(-4, -1), Side.W);

		assertEquals(Side.d2side(-4, 0), Side.W);
		assertEquals(Side.d2side(4, 0), Side.E);
		assertEquals(Side.d2side(0, 4), Side.S);
		assertEquals(Side.d2side(0, -4), Side.N);

		assertEquals(Side.d2side(-4, -4), Side.NW);
		assertEquals(Side.d2side(4, -4), Side.NE);
		assertEquals(Side.d2side(4, 4), Side.SE);
		assertEquals(Side.d2side(-4, 4), Side.SW);

	}

}
