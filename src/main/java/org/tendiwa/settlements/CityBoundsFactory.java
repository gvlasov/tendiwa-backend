package org.tendiwa.settlements;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.Direction;
import org.tendiwa.core.Directions;
import org.tendiwa.demos.CoastlineDemo;
import org.tendiwa.drawing.DrawingCellSet;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.ChebyshevDistanceBufferBorder;
import org.tendiwa.pathfinding.dijkstra.PathTable;

import java.awt.Color;

/**
 * Creates graphs used as a base for a {@link City}.
 */
public class CityBoundsFactory {
    private final CoastlineDemo demo;
    private final CellSet water;

    public CityBoundsFactory(CellSet water, CoastlineDemo demo) {
        this.water = water;
        this.demo = demo;
    }

    /**
     * Culls the cells that will produce intersecting bounding roads.
     *
     * @param bufferBorder
     *         Coastal road's cells.
     * @param start
     *         The point from which the City originated.
     * @return A path table whose bounds end on bufferBorder's cells, but not past them.
     */
    private PathTable cullIntersectingBoundingRoadsCells(
            CellSet bufferBorder,
            Cell start,
            Rectangle boundingRec,
            int radius
    ) {
        return new PathTable(
                start.x,
                start.y,
                (x, y) -> boundingRec.contains(x, y) && !bufferBorder.contains(x, y),
                radius
        ).computeFull();
    }

    private UndirectedGraph<Point2D, Segment2D> computeCityBoundingRoads(
            BoundedCellSet cityShape,
            Cell startCell,
            int radius
    ) {
        CachedCellSet bufferBorder = new CachedCellSet(
                new ChebyshevDistanceBufferBorder(
                        1,
                        (x, y) -> !cityShape.getBounds().contains(x, y) || !cityShape.contains(x, y)
                ),
                cityShape.getBounds()
        ).computeAll();
//        demo.canvas.draw(cityShape.getBounds(), DrawingRectangle.withColor(Color.CYAN));
//        demo.canvas.draw(cityShape, DrawingCellSet.withColor(Color.PINK));
//        demo.canvas.draw(startCell, DrawingCell.withColor(Color.RED));
        demo.canvas.draw(bufferBorder, DrawingCellSet.withColor(Color.MAGENTA));
        PathTable culledTable = cullIntersectingBoundingRoadsCells(
                bufferBorder,
                startCell,
                cityShape.getBounds(),
                radius
        );
        CachedCellSet culledBufferBorder = new CachedCellSet(
                new ChebyshevDistanceBufferBorder(
                        1,
                        culledTable::isCellComputed
                ),
                culledTable.getBounds()
        ).computeAll();
//        demo.canvas.draw(culledBufferBorder, DrawingCellSet.withColor(Color.MAGENTA));

//        canvas.draw(culledTable, DrawingPathTable.withColor(Color.RED));
//        canvas.drawRectangle(cityShape.getBounds(), Color.RED);
//        canvas.draw(bufferBorder, DrawingBoundedCellBufferBorder.withColor(Color.BLUE));
//        canvas.draw(startCell, DrawingCell.withColorAndSize(Color.YELLOW, 3));
//        assert culledBufferBorder.contains(startCell.x, startCell.y);
//        canvas.draw(cityGraph, DrawingGraph.withColorAndVertexSize(ORANGE, 1));
        return bufferBorderToGraph(culledBufferBorder);
    }

    private UndirectedGraph<Point2D, Segment2D> bufferBorderToGraph(CachedCellSet bufferBorder) {
        UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
        BiMap<Cell, Point2D> cell2PointMap = HashBiMap.create();
        ImmutableSet<Cell> borderCells = bufferBorder.toSet();
        for (Cell cell : borderCells) {
            cell2PointMap.put(cell, new Point2D(cell.x, cell.y));
        }
        for (Cell cell : borderCells) {
            graph.addVertex(cell2PointMap.get(cell));
        }
        for (Cell cell : borderCells) {
            for (Direction dir : Directions.CARDINAL_DIRECTIONS) {
                Point2D neighbour = cell2PointMap.get(cell.moveToSide(dir));
                if (graph.containsVertex(neighbour)) {
                    graph.addEdge(cell2PointMap.get(cell), neighbour);
                }
            }
        }
        new EdgeReducer(graph, cell2PointMap).reduceEdges();
//        demo.canvas.draw(graph, DrawingGraph.withColorAndVertexSize(Color.ORANGE, 1));
        return graph;
    }


    /**
     * Creates a new graph that can be used as a base for {@link org.tendiwa.settlements.City}.
     *
     * @param startCell
     *         A cell from which a City originates. Roughly denotes its final position.
     * @param maxCityRadius
     *         A maximum radius of a Rectangle containing resulting City.
     * @return A new graph that can be used as a base for {@link org.tendiwa.settlements.City}.
     * @see org.tendiwa.settlements.CityBuilder
     */
    public UndirectedGraph<Point2D, Segment2D> create(
            BoundedCellSet cityShape,
            Cell startCell,
            int maxCityRadius
    ) {
        if (water.contains(startCell.x, startCell.y)) {
            throw new IllegalArgumentException(
                    "Start cell " + startCell + " must be a ground cell, not water cell"
            );
        }
        return computeCityBoundingRoads(cityShape, startCell, maxCityRadius);
    }
}
