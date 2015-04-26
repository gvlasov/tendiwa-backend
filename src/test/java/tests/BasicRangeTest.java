package tests;

import org.junit.Test;
import org.tendiwa.core.meta.BasicRange;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.range;

public class BasicRangeTest {

	@Test
	public void testIntersection() {
		assertEquals(
			range(0, 10).intersection(range(5, 190)).get(),
			range(5, 10)
		);
		assertEquals(
			range(0, 10).intersection(range(10, 190)).get(),
			range(10, 10)
		);
		assertEquals(
			range(-3, -1).intersection(range(-17, -3)).get(),
			range(-3, -3)
		);
		assertEquals(
			range(-3, -1).intersection(range(-3, 200)).get(),
			range(-3, -1)
		);
		assertEquals(
			range(-300, 300).intersection(range(-600, 600)).get(),
			range(-300, 300)
		);
		assertEquals(
			range(-300, 300).intersection(range(-100, 100)).get(),
			range(-100, 100)
		);
		assertEquals(
			range(-17, 17).intersection(range(-17, 17)).get(),
			range(-17, 17)
		);
		assertEquals(
			range(-17, -17).intersection(range(-17, -17)).get(),
			range(-17, -17)
		);
		assertEquals(
			range(10, 15).intersection(range(9, 13)).get(),
			range(10, 13)
		);
		assertEquals(
			range(9, 13).intersection(range(10, 15)).get(),
			range(10, 13)
		);
	}

	@Test
	public void staticContains() {
		assertTrue(BasicRange.contains(0, 5, 3));
		assertTrue(BasicRange.contains(-4, 1, -2));
		assertTrue(BasicRange.contains(3, 3, 3));
		assertFalse(BasicRange.contains(0, 4, 5));
		assertFalse(BasicRange.contains(-1, -1, 1));
	}

}
