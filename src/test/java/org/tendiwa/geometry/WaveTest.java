package org.tendiwa.geometry;

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
}
