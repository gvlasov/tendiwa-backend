package org.tendiwa.geometry;

import org.junit.Test;
import org.tendiwa.core.meta.Cell;

import static junit.framework.Assert.assertEquals;
import static org.tendiwa.geometry.GeometryPrimitives.cell;

public class WaveTest {
	@Test
	public void iterateOverAllCells() {
		Cell startCell = cell(5, 5);
		Rectangle rectangle = startCell.centerRectangle(13, 8);
		assertEquals(
			Wave
				.from(startCell)
				.goingOver(rectangle::contains)
				.in8Directions()
				.asCellSet(rectangle)
				.stream()
				.count(),
			rectangle.area()
		);
	}

	@Test
	public void iterateOverCellSetBoundedByScalar() {
		Cell centerPoint = new BasicCell(5, 5);
		Rectangle rectangle = centerPoint.centerRectangle(3, 3);
		int numberOfCellsInWave = Wave
			.from(centerPoint)
			.goingOver(rectangle::contains)
			.in8Directions()
			.asCellSet(rectangle.area())
			.toSet()
			.size();
		assertEquals(
			rectangle.area(),
			numberOfCellsInWave
		);
	}

	@Test
	public void iterateOverCellSetBoundedByRectangle() {
		Cell centerPoint = cell(5, 5);
		Rectangle rectangle = centerPoint.centerRectangle(3, 4);
		int numberOfCellsInWave = Wave
			.from(centerPoint)
			.goingOver(rectangle::contains)
			.in8Directions()
			.asCellSet(rectangle)
			.toSet()
			.size();
		assertEquals(rectangle.area(), numberOfCellsInWave);
	}

	/**
	 * This test should throw {@link java.lang.IndexOutOfBoundsException} because bounds for cells ({@code innerRec}
	 * are a subset of all cells {@code outerRec}).
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void wrongBounds() {
		Cell centerPoint = cell(5, 5);
		Rectangle outerRec = centerPoint.centerRectangle(3, 5);
		Rectangle innerRec = centerPoint.centerRectangle(2, 3);
		Wave.from(centerPoint)
			.goingOver(outerRec::contains)
			.in8Directions()
			.asCellSet(innerRec)
			.toSet()
			.size();

	}
}
