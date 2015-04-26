package tests.geometry;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.BasicOrthoCellSegment;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.cell;

public class OrthoCellSegmentTest {

	/**
	 * Test iterating over a {@link org.tendiwa.geometry.BasicOrthoCellSegment} as a collection of points: points
	 * should go as axis grows, and each point of a Segment should be iterated
	 * over.
	 */
	@Test
	public void testIterableVertical() {
		assertEquals(
			Lists.newArrayList(
				new BasicOrthoCellSegment(4, 7, 3, Orientation.VERTICAL)
			),
			Arrays.asList(
				cell(4, 7),
				cell(4, 8),
				cell(4, 9)
			)
		);
	}

	/**
	 * @see OrthoCellSegmentTest#testIterableVertical()
	 */
	@Test
	public void testIterableHorizontal() {
		assertEquals(
			Lists.newArrayList(
				new BasicOrthoCellSegment(8, 19, 5, Orientation.HORIZONTAL)
			),
			Arrays.asList(
				cell(8, 19),
				cell(9, 19),
				cell(10, 19),
				cell(11, 19),
				cell(12, 19)
			)
		);
	}
}
