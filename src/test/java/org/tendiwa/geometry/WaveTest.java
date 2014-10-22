package org.tendiwa.geometry;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.tendiwa.geometry.DSL.cell;

public class WaveTest {
	@Test
	public void iterateOverAllCells() {
		Cell startCell = cell(5, 5);
		int width = 13;
		int height = 8;
		Rectangle rectangle = Recs.rectangleByCenterPoint(startCell, width, height);
		int i = 0;
		//noinspection UnusedDeclaration
		for (Cell cell : Wave.from(startCell).goingOver(rectangle::contains).in8Directions()) {
			i++;
		}
		assertEquals(i, width * height);
	}

	@Test
	public void iterateOverCellSetBoundedByScalar() {
		Cell centerPoint = new Cell(5, 5);
		Rectangle rectangle = Recs.rectangleByCenterPoint(centerPoint, 3, 3);
		int numberOfCellsInWave = Wave
			.from(centerPoint)
			.goingOver(rectangle::contains)
			.in8Directions()
			.asCellSet(rectangle.width * rectangle.height)
			.toSet()
			.size();
		assertEquals(9, numberOfCellsInWave);
	}

	@Test
	public void iterateOverCellSetBoundedByRectangle() {
		Cell centerPoint = new Cell(5, 5);
		Rectangle rectangle = Recs.rectangleByCenterPoint(centerPoint, 3, 4);
		int numberOfCellsInWave = Wave
			.from(centerPoint)
			.goingOver(rectangle::contains)
			.in8Directions()
			.asCellSet(rectangle)
			.toSet()
			.size();
		assertEquals(12, numberOfCellsInWave);
	}

	/**
	 * This test should throw {@link java.lang.IndexOutOfBoundsException} because bounds for cells ({@code innerRec}
	 * are a subset of all cells {@code outerRec}).
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void wrongBounds() {
		Cell centerPoint = new Cell(5, 5);
		Rectangle outerRec = Recs.rectangleByCenterPoint(centerPoint, 3, 5);
		Rectangle innerRec = Recs.rectangleByCenterPoint(centerPoint, 2, 3);
		Wave.from(centerPoint)
			.goingOver(outerRec::contains)
			.in8Directions()
			.asCellSet(innerRec)
			.toSet()
			.size();

	}
}
