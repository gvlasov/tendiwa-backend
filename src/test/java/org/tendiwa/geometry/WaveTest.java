package org.tendiwa.geometry;

import groovy.sql.InOutParameter;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.tendiwa.geometry.DSL.cell;

public class WaveTest {
    @Test
    public void testHandle() {
        Cell startCell = cell(5, 5);
        int width = 13;
        int height = 8;
        Rectangle rectangle = Recs.rectangleByCenterPoint(startCell, width, height);
        int i = 0;
        for (Cell ignored : Wave.from(startCell).goingOver(rectangle::contains)) {
            i++;
        }
        assertEquals(i, width * height);
    }

    @Test
    public void shouldIterateOverCollectedCells() {
        Cell centerPoint = new Cell(5, 5);
        Rectangle rectangle = Recs.rectangleByCenterPoint(centerPoint, 3, 3);
        int numberOfCellsInWave = Wave
                .from(centerPoint)
                .goingOver(rectangle::contains)
                .asCellSet(rectangle.width*rectangle.height)
                .toSet()
                .size();
        assertEquals(9, numberOfCellsInWave);
    }

    @Test
    public void shouldIterateOverCollectedCellsInRectangle() {
        Cell centerPoint = new Cell(5, 5);
        Rectangle rectangle = Recs.rectangleByCenterPoint(centerPoint, 3, 4);
        int numberOfCellsInWave = Wave
                .from(centerPoint)
                .goingOver(rectangle::contains)
                .asCellSet(rectangle)
                .toSet()
                .size();
        assertEquals(12, numberOfCellsInWave);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowArrayOutOfBounds() {
        Cell centerPoint = new Cell(5, 5);
        Rectangle rectangle = Recs.rectangleByCenterPoint(centerPoint, 3, 5);
        Wave.from(centerPoint)
                .goingOver(rectangle::contains)
                .asCellSet(Recs.rectangleByCenterPoint(centerPoint, 2, 3))
                .toSet()
                .size();

    }
}
