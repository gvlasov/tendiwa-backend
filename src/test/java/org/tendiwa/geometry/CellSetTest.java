package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.tendiwa.core.CardinalDirection;
import org.w3c.dom.CDATASection;

import static org.junit.Assert.*;

public class CellSetTest {
    @Test
    public void testAnd() throws Exception {
        CellSet cells = FiniteCellSet.of(new Cell(1, 2))
                .and(FiniteCellSet.of(new Cell(1, 3), new Cell(1, 2)))
                .and(FiniteCellSet.of(new Cell(1, 4), new Cell(1, 2)));

        assertTrue(cells.contains(1, 2));
        assertFalse(cells.contains(1, 3));
        assertFalse(cells.contains(1, 4));

    }

    @Test
    public void testOr() throws Exception {

        CellSet cells = FiniteCellSet.of(new Cell(1, 2))
                .or(FiniteCellSet.of(new Cell(1, 3)))
                .or(FiniteCellSet.of(new Cell(1, 4)));
        assertTrue(cells.contains(1, 2));
        assertTrue(cells.contains(1, 3));
        assertTrue(cells.contains(1, 4));
    }

    @Test
    public void testToCellSet() throws Exception {
        CellSet cellSet = ImmutableSet.of(
                new Cell(1, 2),
                new Cell(2, 3),
                new Cell(3, 4)
        )
                .stream()
                .map(c -> c.moveToSide(CardinalDirection.W))
                .collect(CellSet.toCellSet());
        assertFalse(cellSet.contains(1, 2));
        assertFalse(cellSet.contains(2, 3));
        assertFalse(cellSet.contains(3, 4));
        assertTrue(cellSet.contains(0, 2));
        assertTrue(cellSet.contains(1, 3));
        assertTrue(cellSet.contains(2, 4));
    }
}
