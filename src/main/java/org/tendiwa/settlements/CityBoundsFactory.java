package org.tendiwa.settlements;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Direction;
import org.tendiwa.core.Directions;
import org.tendiwa.demos.CoastlineDemo;
import org.tendiwa.drawing.DrawingBoundedCellBufferBorder;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.ChebyshevDistanceCellBufferBorder;
import org.tendiwa.pathfinding.dijkstra.PathTable;

import java.awt.*;
import java.util.*;

/**
 * Creates graphs used as a base for a City.
 */
public class CityBoundsFactory {
    private final Rectangle worldRec;
    private final CellSet water;

    public CityBoundsFactory(Rectangle worldRec, CellSet water) {
        this.water = water;
        this.worldRec = worldRec;
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
            Rectangle worldRec,
            int radius
    ) {
        return new PathTable(
                start.x,
                start.y,
                (x, y) -> worldRec.contains(x, y) && !bufferBorder.contains(x, y),
                radius
        ).computeFull();
    }

    private UndirectedGraph<Point2D, Segment2D> computeCityBoundingRoads(
            BoundedCellSet cityShape,
            Rectangle worldRec,
            Cell startCell,
            int radius
    ) {
        CachedCellSet bufferBorder = new CachedCellSet(
                new ChebyshevDistanceCellBufferBorder(
                        1,
                        (x, y) -> !cityShape.getBounds().contains(x, y) || !cityShape.contains(x, y)
                ),
                cityShape.getBounds()
        ).computeAll();
        PathTable culledTable = cullIntersectingBoundingRoadsCells(
                bufferBorder,
                startCell,
                worldRec,
                radius
        );
        CachedCellSet culledBufferBorder = new CachedCellSet(
                new ChebyshevDistanceCellBufferBorder(
                        1,
                        culledTable::isCellComputed
                ),
                culledTable.getBounds()
        ).computeAll();
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
        ImmutableList<Cell> borderCells = bufferBorder.toList();
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
        return graph;
    }


    private class EdgeReducer {

        private final CardinalDirection[] growingDirs = {CardinalDirection.N, CardinalDirection.E};
        private UndirectedGraph<Point2D, Segment2D> graph;
        private BiMap<Cell, Point2D> map;

        public EdgeReducer(UndirectedGraph<Point2D, Segment2D> graph, BiMap<Cell, Point2D> map) {

            this.graph = graph;
            this.map = map;
        }

        private Collection<Cell> findIntersectionCells() {
            Collection<Cell> answer = new HashSet<>();
            for (Cell cell : map.keySet()) {
                int neighbourCells = 0;
                for (Direction dir : CardinalDirection.values()) {
                    if (map.containsKey(cell.moveToSide(dir))) {
                        neighbourCells++;
                    }
                }
                if (neighbourCells > 2) {
                    answer.add(cell);
                }
            }
            return answer;
        }

        private void reduceEdges() {
            boolean changesMade;
            Collection<Point2D> finalVertices = new HashSet<>();
            Set<Point2D> vertices = ImmutableSet.copyOf(graph.vertexSet());
            do {
                changesMade = false;
                Collection<Cell> intersectionCells = findIntersectionCells();
                for (Point2D point : vertices) {
                    if (!graph.containsVertex(point) || finalVertices.contains(point)) {
                        continue;
                    }
                    Cell graphCell = map.inverse().get(point);
                    for (CardinalDirection dir : growingDirs) {
                        int combinedEdgeLength = 1;
                        Cell movedCell = graphCell;
                        while (true) {
                            movedCell = movedCell.moveToSide(dir);
                            if (map.containsKey(movedCell) && graph.containsVertex(map.get(movedCell))) {
                                combinedEdgeLength++;
                                if (intersectionCells.contains(movedCell)) {
                                    break;
                                }
                            } else {
                                movedCell = movedCell.moveToSide(dir.opposite());
                                break;
                            }
                        }
                        Cell oppositeMovedCell = graphCell;
                        while (true) {
                            oppositeMovedCell = oppositeMovedCell.moveToSide(dir.opposite());
                            if (map.containsKey(oppositeMovedCell) && graph.containsVertex(map.get(oppositeMovedCell))) {
                                combinedEdgeLength++;
                                if (intersectionCells.contains(oppositeMovedCell)) {
                                    break;
                                }
                            } else {
                                oppositeMovedCell = oppositeMovedCell.moveToSide(dir);
                                break;
                            }
                        }
                        if (combinedEdgeLength > 2) {
                            for (
                                    Cell cell = movedCell.moveToSide(dir.opposite());
                                    !cell.equals(oppositeMovedCell);
                                    cell = cell.moveToSide(dir.opposite())
                                    ) {
                                graph.removeVertex(map.get(cell));
                            }
                            changesMade = true;
//                            canvas.drawCell(oppositeMovedCell, YELLOW);
//                            canvas.drawCell(movedCell, RED);
                            graph.addEdge(map.get(movedCell), map.get(oppositeMovedCell));
                            finalVertices.add(map.get(movedCell));
                            finalVertices.add(map.get(oppositeMovedCell));
                        } else if (combinedEdgeLength == 2) {
//                            if (!graph.containsEdge(map.get(movedCell), map.get(oppositeMovedCell))) {
//                                canvas.draw(movedCell, DrawingCell.withColorAndSize(Color.BLACK, 1));
//                                canvas.draw(oppositeMovedCell, DrawingCell.withColorAndSize(Color.BLACK, 1));
//                                throw new AssertionError(movedCell + " " + oppositeMovedCell);
//                            }
                        }
                    }
                }

            } while (changesMade);
        }
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
        return computeCityBoundingRoads(cityShape, worldRec, startCell, maxCityRadius);
    }
}
