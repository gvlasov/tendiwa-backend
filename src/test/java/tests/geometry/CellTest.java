package tests.geometry;

import org.junit.Test;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Cells;

import static org.junit.Assert.*;

public class CellTest {

	/**
	 * Creates a new Cell object using static and dynamic coordinate parameters instead of x and y coordinates.
	 *
	 * @see Cell#getDynamicCoord(org.tendiwa.core.Orientation) For what a dynamic coordinate is.
	 * @see Cell#getStaticCoord(org.tendiwa.core.Orientation) For what a static coordinate is.
	 */
	@Test
	public void createNewCellFromStaticAndDynamic() {
		assertEquals(
			Cells.fromStaticAndDynamic(7, 9, Orientation.VERTICAL),
			new Cell(7, 9)
		);
		assertEquals(
			Cells.fromStaticAndDynamic(7, 9, Orientation.HORIZONTAL),
			new Cell(9, 7)
		);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void move() {
		Cell point = new Cell(6, 7)
			.moveToSide(Directions.E) // +1 by x
			.moveToSide(Directions.NW) // -1 by y, -1 by x
			.moveToSide(Directions.SW); // +1 by y, -1 by x
		assertEquals(point, new Cell(5, 7));
	}

}
