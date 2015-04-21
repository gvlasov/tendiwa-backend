package tests;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.tendiwa.core.meta.Range;
import org.tendiwa.core.meta.BasicRange;

import java.util.Collection;

import static org.junit.Assert.*;

public class BasicRangeTest {

	@Test
	public void testIntersection() {
		assertEquals(
			new BasicRange(0, 10).intersection(new BasicRange(5, 190)),
			new BasicRange(5, 10)
		);
		assertEquals(
			new BasicRange(0, 10).intersection(new BasicRange(10, 190)),
			new BasicRange(10, 10)
		);
		assertEquals(
			new BasicRange(-3, -1).intersection(new BasicRange(-17, -3)),
			new BasicRange(-3, -3)
		);
		assertEquals(
			new BasicRange(-3, -1).intersection(new BasicRange(-3, 200)),
			new BasicRange(-3, -1)
		);
		assertEquals(
			new BasicRange(-300, 300).intersection(new BasicRange(-600, 600)),
			new BasicRange(-300, 300)
		);
		assertEquals(
			new BasicRange(-300, 300).intersection(new BasicRange(-100, 100)),
			new BasicRange(-100, 100)
		);
		assertEquals(
			new BasicRange(-17, 17).intersection(new BasicRange(-17, 17)),
			new BasicRange(-17, 17)
		);
		assertEquals(
			new BasicRange(-17, -17).intersection(new BasicRange(-17, -17)),
			new BasicRange(-17, -17)
		);
		assertEquals(
			new BasicRange(10, 15).intersection(new BasicRange(9, 13)),
			new BasicRange(10, 13)
		);
		assertEquals(
			new BasicRange(9, 13).intersection(new BasicRange(10, 15)),
			new BasicRange(10, 13)
		);
	}

	@Test
	public void splitWithRanges() {
		Collection<Range> ranges = Sets
			.newHashSet(new BasicRange(0, 10).splitWithRanges(ImmutableSet.of(
				new BasicRange(2, 5),
				new BasicRange(7, 8))));
		assertTrue(ranges.contains(new BasicRange(0, 1)));
		assertTrue(ranges.contains(new BasicRange(6, 6)));
		assertTrue(ranges.contains(new BasicRange(9, 10)));
		assertTrue(ranges.size() == 3);

		ranges = Sets.newHashSet(new BasicRange(-12, 22)
			.splitWithRanges(ImmutableSet.of(
				new BasicRange(-12, 1),
				new BasicRange(7, 22))));
		assertTrue(ranges.contains(new BasicRange(2, 6)));
		assertTrue(ranges.size() == 1);
		ranges = Sets.newHashSet(new BasicRange(-100, 100)
			.splitWithRanges(ImmutableSet.of(new BasicRange(0, 0))));
		assertTrue(ranges.contains(new BasicRange(-100, -1)));
		assertTrue(ranges.contains(new BasicRange(1, 100)));
		assertTrue(ranges.size() == 2);
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
