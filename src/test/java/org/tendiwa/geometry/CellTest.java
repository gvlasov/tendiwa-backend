package org.tendiwa.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {
    @Test
    public void chebyshevDistanceToNegative() {
        int distance = new Cell(30, 30).chebyshevDistanceTo(new Cell(-60, -60));
        assertEquals(distance, 90);
    }

    @Test
    public void chebyshevDistanceToItself() {
        int distance = new Cell(30, 30).chebyshevDistanceTo(new Cell(30, 30));
        assertEquals(distance, 0);
    }
}

