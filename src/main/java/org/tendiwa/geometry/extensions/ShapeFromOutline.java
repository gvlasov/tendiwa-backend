package org.tendiwa.geometry.extensions;

import com.sun.media.sound.SoftLowFrequencyOscillator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.*;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.util.stream.Collectors;

import static java.util.Objects.*;

public final class ShapeFromOutline {
	private ShapeFromOutline() {

	}
    public static BoundedCellSet from(UndirectedGraph<Point2D, Segment2D> outline) {

        FiniteCellSet edgeCells = requireNonNull(outline)
                .edgeSet()
                .stream()
                .map(Segment2D.toCellList())
                .flatMap(a->a.stream())
                .distinct()
                .collect(CellSet.toCellSet());
        Rectangle graphBounds = Recs.boundsOfCells(edgeCells);
        return new CachedCellSet(
                new MinimumCycleBasis<>(outline, Point2DVertexPositionAdapter.get())
                        .minimalCyclesSet()
                        .stream()
                        .map(ShapeFromOutline::polygonCells)
                        .reduce(edgeCells, (a, b) -> a.or(b)),
                graphBounds
        );


    }

    /**
     * @param cycle
     *         A minimal cycle.
     * @return A cell that is within a cycle.
     */
    private static CellSet polygonCells(MinimalCycle<Point2D, Segment2D> cycle) {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coordinates = cycle.vertexList()
                .stream()
                .map(p -> new Coordinate(p.x, p.y))
                .collect(Collectors.toList())
                .toArray(new Coordinate[cycle.vertexList().size() + 1]);
        coordinates[coordinates.length - 1] = coordinates[0];

        Polygon polygon = gf.createPolygon(gf.createLinearRing(coordinates), null);
        return (x, y) -> polygon.contains(gf.createPoint(new Coordinate(x, y)));
    }
}
