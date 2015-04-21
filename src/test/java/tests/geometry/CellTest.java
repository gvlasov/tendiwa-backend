package tests.geometry;

import org.junit.Test;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.Cells;

import static org.junit.Assert.*;

public class CellTest {

	/**
	 * Creates a new Cell object using static and dynamic coordinate parameters instead of x and y coordinates.
	 *
	 * @see org.tendiwa.geometry.BasicCell#getDynamicCoord(org.tendiwa.core.Orientation) For what a dynamic coordinate is.
	 * @see org.tendiwa.geometry.BasicCell#getStaticCoord(org.tendiwa.core.Orientation) For what a static coordinate is.
	 */
	@Test
	public void createNewCellFromStaticAndDynamic() {
		assertEquals(
			Cells.fromStaticAndDynamic(7, 9, Orientation.VERTICAL),
			new BasicCell(7, 9)
		);
		assertEquals(
			Cells.fromStaticAndDynamic(7, 9, Orientation.HORIZONTAL),
			new BasicCell(9, 7)
		);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void move() {
		BasicCell point = new BasicCell(6, 7)
			.moveToSide(Directions.E) // +1 by x
			.moveToSide(Directions.NW) // -1 by y, -1 by x
			.moveToSide(Directions.SW); // +1 by y, -1 by x
		assertEquals(point, new BasicCell(5, 7));
	}

}
