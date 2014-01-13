package tests;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import org.tendiwa.core.meta.Range;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class RangeTest {

	@Test
	public void testIntersection() {
		assertEquals(
			new Range(0, 10).intersection(new Range(5, 190)),
			new Range(5, 10));
		assertEquals(
			new Range(0, 10).intersection(new Range(10, 190)),
			new Range(10, 10));
		assertEquals(
			new Range(-3, -1).intersection(new Range(-17, -3)),
			new Range(-3, -3));
		assertEquals(
			new Range(-3, -1).intersection(new Range(-3, 200)),
			new Range(-3, -1));
		assertEquals(
			new Range(-300, 300).intersection(new Range(-600, 600)),
			new Range(-300, 300));
		assertEquals(
			new Range(-300, 300).intersection(new Range(-100, 100)),
			new Range(-100, 100));
		assertEquals(
			new Range(-17, 17).intersection(new Range(-17, 17)),
			new Range(-17, 17));
		assertEquals(
			new Range(-17, -17).intersection(new Range(-17, -17)),
			new Range(-17, -17));
		assertEquals(
			new Range(10, 15).intersection(new Range(9, 13)),
			new Range(10, 13));
		assertEquals(
			new Range(9, 13).intersection(new Range(10, 15)),
			new Range(10, 13));
	}
	@Test
	public void testIntersectionIntegers() {
		assertEquals(Range.lengthOfIntersection(0, 10, 5, 190), 6);
		assertEquals(Range.lengthOfIntersection(0, 10, 10, 190), 1);
		assertEquals(Range.lengthOfIntersection(-3, -1, -17, -3), 1);
		assertEquals(Range.lengthOfIntersection(-3, -1, -3, 200), 3);
		assertEquals(Range.lengthOfIntersection(-300, 300, -600, 600), 600 + 1);
		assertEquals(Range.lengthOfIntersection(-300, 300, -100, 100), 200 + 1);
		assertEquals(Range.lengthOfIntersection(-17, 17, -17, 17), 17 * 2 + 1);
		assertEquals(Range.lengthOfIntersection(-17, -17, -17, 17), 1);
	}
	@Test
	public void splitWithRanges() {
		Collection<Range> ranges = Sets
			.newHashSet(new Range(0, 10).splitWithRanges(ImmutableSet.of(
				new Range(2, 5),
				new Range(7, 8))));
		assertTrue(ranges.contains(new Range(0, 1)));
		assertTrue(ranges.contains(new Range(6, 6)));
		assertTrue(ranges.contains(new Range(9, 10)));
		assertTrue(ranges.size() == 3);

		ranges = Sets.newHashSet(new Range(-12, 22)
			.splitWithRanges(ImmutableSet.of(
				new Range(-12, 1),
				new Range(7, 22))));
		assertTrue(ranges.contains(new Range(2, 6)));
		assertTrue(ranges.size() == 1);
		ranges = Sets.newHashSet(new Range(-100, 100)
			.splitWithRanges(ImmutableSet.of(new Range(0, 0))));
		assertTrue(ranges.contains(new Range(-100, -1)));
		assertTrue(ranges.contains(new Range(1, 100)));
		assertTrue(ranges.size() == 2);
	}
	@Test
	public void staticContains() {
		assertTrue(Range.contains(0, 5, 3));
		assertTrue(Range.contains(-4, 1, -2));
		assertTrue(Range.contains(3, 3, 3));
		assertFalse(Range.contains(0, 4, 5));
		assertFalse(Range.contains(-1, -1, 1));
	}

}
