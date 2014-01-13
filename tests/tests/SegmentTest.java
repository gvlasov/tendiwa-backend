package tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.tendiwa.core.EnhancedPoint;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.Segment;

import com.google.common.collect.Lists;

public class SegmentTest {

	/**
	 * Test iterating over a {@link Segment} as a collection of points: points
	 * should go as axis grows, and each point of a Segment should be iterated
	 * over.
	 */
	@Test
	public void testIterableVertical() {
		Segment segment = new Segment(4, 7, 3, Orientation.VERTICAL);
		List<EnhancedPoint> cellsOfSegment = Lists.newArrayList(segment);
		List<EnhancedPoint> equalCells = Arrays.asList(new EnhancedPoint[] {
			new EnhancedPoint(4, 7),
			new EnhancedPoint(4, 8),
			new EnhancedPoint(4, 9)
		});
		assertEquals(cellsOfSegment, equalCells);
	}
	/**
	 * @see SegmentTest#testIterableVertical()
	 */
	@Test
	public void testIterableHorizontal() {
		Segment segment = new Segment(8, 19, 5, Orientation.HORIZONTAL);
		List<EnhancedPoint> cellsOfSegment = Lists.newArrayList(segment);
		List<EnhancedPoint> equalCells = Arrays.asList(new EnhancedPoint[] {
			new EnhancedPoint(8, 19),
			new EnhancedPoint(9, 19),
			new EnhancedPoint(10, 19),
			new EnhancedPoint(11, 19),
			new EnhancedPoint(12, 19)
		});
		assertEquals(cellsOfSegment, equalCells);
	}
}
