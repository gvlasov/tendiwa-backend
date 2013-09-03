package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import tendiwa.core.meta.Range;

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
			.splitWithRanges(ImmutableSet.of(
				new Range(0, 0))));
		assertTrue(ranges.contains(new Range(-100, -1)));
		assertTrue(ranges.contains(new Range(1, 100)));
		assertTrue(ranges.size() == 2);

	}

}
