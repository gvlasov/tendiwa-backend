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
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellBufferBorder;
import org.tendiwa.geometry.extensions.ChebyshevDistanceCellBufferBorder;
import org.tendiwa.pathfinding.dijkstra.PathTable;

import java.util.*;
import java.util.function.BiFunction;

public class CityBoundsFactory {
    private final Rectangle worldRec;
    private BiFunction<Integer, Integer, Boolean> isWater;

    public CityBoundsFactory(int worldWidth, int worldHeight, BiFunction<Integer, Integer, Boolean> isWater) {
        this.isWater = isWater;
        worldRec = new Rectangle(0, 0, worldWidth, worldHeight);

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
            CellBufferBorder bufferBorder,
            Cell start,
            Rectangle worldRec,
            int radius
    ) {
        return new PathTable(
                start.x,
                start.y,
                (x, y) -> worldRec.contains(x, y) && !bufferBorder.isBufferBorder(x, y),
                radius
        ).computeFull();
    }

    private UndirectedGraph<Point2D, Segment2D> computeCityBoundingRoads(
            PathTable table,
            Rectangle worldRec,
            Cell startCell,
            int radius
    ) {
        CachedCellBufferBorder bufferBorder = new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(
                        1,
                        (x, y) -> !table.isCellComputed(x, y)
                ),
                table.getBounds()
        ).computeAll();
        BoundedCellBufferBorder oppositeBufferBorder = new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(
                        1,
                        table::isCellComputed
                ),
                table.getBounds().stretch(1)
        );
        PathTable culledTable = cullIntersectingBoundingRoadsCells(
                bufferBorder,
                startCell,
                worldRec,
                radius
        );
        CachedCellBufferBorder culledBufferBorder = new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(
                        1,
                        culledTable::isCellComputed
                ),
                culledTable.getBounds()
        ).computeAll();
//        canvas.draw(culledTable, DrawingPathTable.withColor(Color.RED));
//        canvas.draw(culledBufferBorder, DrawingBoundedCellBufferBorder.withColor(BLUE));
//        canvas.draw(startCell, DrawingCell.withColorAndSize(Color.YELLOW, 3));
//        assert culledBufferBorder.isBufferBorder(startCell.x, startCell.y);
        //        canvas.draw(cityGraph, DrawingGraph.withColorAndVertexSize(ORANGE, 1));
        return bufferBorderToGraph(culledBufferBorder);
    }

    private UndirectedGraph<Point2D, Segment2D> bufferBorderToGraph(CachedCellBufferBorder bufferBorder) {
        UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
        BiMap<Cell, Point2D> cell2PointMap = HashBiMap.create();
        ImmutableList<Cell> borderCells = bufferBorder.cellList();
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
     * Computes buffer border used to find City's border.
     *
     * @param startCell
     * @param radius
     * @param depth
     * @return
     * @see #getDeepCoast(org.tendiwa.geometry.Rectangle, int)
     */
    private CachedCellBufferBorder getCoast(Cell startCell, int radius, int depth) {
        return new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(depth, isWater),
                Recs.rectangleByCenterPoint(startCell, radius * 2 + 1, radius * 2 + 1)
        );
    }

    /**
     * Computes buffer border used to find city's center.
     *
     * @param worldRec
     *         A Rectangle starting at 0:0 with width and height of the world.
     * @return A buffer border that is
     */
    private CachedCellBufferBorder getDeepCoast(Rectangle worldRec, int depth) {
        CachedCellBufferBorder bufferBorder = new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(
                        depth,
                        (x, y) -> worldRec.contains(x, y) && isWater.apply(x, y)
                ),
                worldRec
        );
        if (bufferBorder.cellList().isEmpty()) {
            throw new SettlementGenerationException(
                    "Buffer border of depth " + depth + " is so deep it is non-existent. Try decreasing depth"
            );
        }
        return bufferBorder;
    }

    private PathTable computeCityShape(
            Rectangle worldRec,
            Cell startCell,
            CellBufferBorder coast,
            int radius
    ) {
        return new PathTable(
                startCell.x,
                startCell.y,
                (x, y) -> worldRec.contains(x, y) && !coast.isBufferBorder(x, y),
                radius
        ).computeFull();
    }


    /**
     * Translates a CellBufferBorder to a List of {@link Cell}s.
     *
     * @param worldRec
     * @param deepBufferBorder
     * @return
     */
    private List<Cell> computeCoastRoad(Rectangle worldRec, CachedCellBufferBorder deepBufferBorder) {
        Collection<Cell> borderCells = new HashSet<>(deepBufferBorder.cellList());
        assert !borderCells.isEmpty() : "Deep buffer border is too deep";
        List<Cell> coastRoadCells = new ArrayList<>();
        while (!borderCells.isEmpty()) {
            Cell nextBorderCell = borderCells.iterator().next();
            borderCells.remove(nextBorderCell);
            PathTable table = new PathTable(
                    nextBorderCell.x,
                    nextBorderCell.y,
                    (x, y) -> worldRec.contains(x, y) && deepBufferBorder.isBufferBorder(x, y),
                    360
            );
            table.computeFull();
            for (Cell cell : table) {
                borderCells.remove(cell);
                if (worldRec.contains(cell)) {
                    coastRoadCells.add(cell);
                }
            }
        }
        return coastRoadCells;
    }

    public UndirectedGraph<Point2D, Segment2D> create(Cell startCell, int maxCityRadius, int minDistanceToWater) {

        CachedCellBufferBorder deepBufferBorder = getDeepCoast(worldRec, minDistanceToWater + 2);
        //            canvas.draw(deepBufferBorder, DrawingBoundedCellBufferBorder.withColor(Color.YELLOW));
        PathTable table;
        table = computeCityShape(
                worldRec,
                startCell,
                getCoast(startCell, maxCityRadius, minDistanceToWater),
                maxCityRadius
        );
        return computeCityBoundingRoads(table, worldRec, startCell, maxCityRadius);
    }
}
