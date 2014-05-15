package org.tendiwa.geometry.extensions;

import org.junit.Test;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.FiniteCellSet;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.tendiwa.geometry.DSL.rectangle;

public class IntershapeNetworkTest {
    @Test
    public void network() {
        List<FiniteCellSet> shapeExitSets = asList(
                FiniteCellSet.of(new Cell(1, 1)),
                FiniteCellSet.of(new Cell(4, 9)),
                FiniteCellSet.of(new Cell(8, 8)),
                FiniteCellSet.of(new Cell(5, 5), new Cell(5, 6))
        );
        int numberOfEdges = IntershapeNetwork.builder()
                .withShapeExits(shapeExitSets)
                .withWalkableCells(
                        (x, y) -> rectangle(10, 10).contains(x, y)
                )
                .build()
                .getGraph()
                .edgeSet()
                .size();
        assertTrue(numberOfEdges > 3);
    }
}
