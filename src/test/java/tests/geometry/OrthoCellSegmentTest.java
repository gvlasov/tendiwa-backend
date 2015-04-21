package tests.geometry;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.BasicOrthoCellSegment;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OrthoCellSegmentTest {

	/**
	 * Test iterating over a {@link org.tendiwa.geometry.BasicOrthoCellSegment} as a collection of points: points
	 * should go as axis grows, and each point of a Segment should be iterated
	 * over.
	 */
	@Test
	public void testIterableVertical() {
		BasicOrthoCellSegment segment = new BasicOrthoCellSegment(4, 7, 3, Orientation.VERTICAL);
		List<BasicCell> cellsOfSegment = Lists.newArrayList(segment);
		List<BasicCell> equalCells = Arrays.asList(new BasicCell[]{
			new BasicCell(4, 7),
			new BasicCell(4, 8),
			new BasicCell(4, 9)
		});
		assertEquals(cellsOfSegment, equalCells);
	}

	/**
	 * @see OrthoCellSegmentTest#testIterableVertical()
	 */
	@Test
	public void testIterableHorizontal() {
		BasicOrthoCellSegment segment = new BasicOrthoCellSegment(8, 19, 5, Orientation.HORIZONTAL);
		List<BasicCell> cellsOfSegment = Lists.newArrayList(segment);
		List<BasicCell> equalCells = Arrays.asList(new BasicCell[]{
			new BasicCell(8, 19),
			new BasicCell(9, 19),
			new BasicCell(10, 19),
			new BasicCell(11, 19),
			new BasicCell(12, 19)
		});
		assertEquals(cellsOfSegment, equalCells);
	}
}
