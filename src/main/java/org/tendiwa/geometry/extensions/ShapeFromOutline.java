package org.tendiwa.geometry.extensions;

import com.google.common.collect.Lists;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.*;
import org.tendiwa.graphs.MinimalCycle;

import static java.util.Objects.*;

public class ShapeFromOutline {
    public static BoundedCellSet from(UndirectedGraph<Point2D, Segment2D> outline) {

        FiniteCellSet edgeCells = requireNonNull(outline)
                .edgeSet()
                .stream()
                .flatMap(
                        e -> Lists.newArrayList(
                                CellSegment.vector(e.start.toCell(), e.end.toCell())
                        ).stream()
                )
                .collect(CellSet.toCellSet());
//        new MinimumCycleBasis<>(outline, Point2DVertexPositionAdapter.get())
//                .minimalCyclesSet()
//                .stream()
//                .map(ShapeFromOutline::findCellWithin)
//                .flatMap(cell->Wave.from(cell).goingOver(edgeCells).)

        return null;
    }

    /**
     * @param cycle
     *         A minimal cycle.
     * @return A cell that is within a cycle.
     */
    private static Cell findCellWithin(MinimalCycle<Point2D, Segment2D> cycle) {


        return null;
    }
}
