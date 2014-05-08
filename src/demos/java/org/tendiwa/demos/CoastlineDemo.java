package org.tendiwa.demos;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Direction;
import org.tendiwa.core.Directions;
import org.tendiwa.demos.settlements.CityDrawer;
import org.tendiwa.drawing.*;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.CachedCellBufferBorder;
import org.tendiwa.geometry.extensions.ChebyshevDistanceCellBufferBorder;
import org.tendiwa.noise.Noise;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.pathfinding.dijkstra.PostConditionPathTable;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.awt.Color.*;

public class CoastlineDemo implements Runnable {
    public static final int MAX_CITY_DEPTH = 360;
    @Inject
    @Named("scale2")
    TestCanvas canvas;
    private final int width = 300;
    private final int height = 200;
    private final int radius = 58;

    public static void main(String[] args) {
        Demos.run(
                CoastlineDemo.class,
                new DrawingModule(),
                new LargerScaleCanvasModule()
        );
    }

    @Override
    public void run() {
//        for (int i = 1; i < 12; i++) {
        drawTerrain();
        Rectangle worldRec = new Rectangle(0, 0, width, height);
        CachedCellBufferBorder cachedCellBufferBorder = computeBufferBorder(worldRec);
        List<Cell> roadCells = computeCoastRoad(worldRec, cachedCellBufferBorder);
        Cell startCell = roadCells.get(0);
        PathTable table;
        table = computeCityShape(
                worldRec,
                startCell,
                getCoast(startCell, radius, 6),
                radius
        );
        System.out.println(table.getBounds());

        computeCityBoundingRoads(table, worldRec);
        canvas.clear();
//        }
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
            Rectangle worldRec
    ) {
        return new PostConditionPathTable(
                start.x,
                start.y,
                (x, y) -> worldRec.contains(x, y) && !bufferBorder.isBufferBorder(x, y),
                radius
        ).computeFull();
    }

    private void computeCityBoundingRoads(PathTable table, Rectangle worldRec) {
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
                table.getStart(),
                worldRec
        );
        CachedCellBufferBorder culledBufferBorder = new CachedCellBufferBorder(
                (x, y) -> !culledTable.isCellComputed(x, y),
                culledTable.getBounds()
        );
        canvas.draw(culledTable, DrawingPathTable.withColor(Color.RED));
//        canvas.draw(bufferBorder, DrawingBoundedCellBufferBorder.withColor(BLUE));
        UndirectedGraph<Point2D, Line2D> cityGraph = bufferBorderToGraph(culledBufferBorder);
        System.out.println(cityGraph.vertexSet().size());
//        canvas.draw(cityGraph, DrawingGraph.withColorAndVertexSize(ORANGE, 1));

        City city = new CityBuilder(cityGraph)
                .withDefaults()
                .withMaxStartPointsPerCycle(200)
                .build();
        canvas.draw(city, new CityDrawer());
    }

    private UndirectedGraph<Point2D, Line2D> bufferBorderToGraph(CachedCellBufferBorder bufferBorder) {
        UndirectedGraph<Point2D, Line2D> graph = new SimpleGraph<>(Line2D::new);
        BiMap<Cell, Point2D> cell2PointMap = HashBiMap.create();
        for (Cell cell : bufferBorder.cellList()) {
            cell2PointMap.put(cell, new Point2D(cell.x, cell.y));
        }
        for (Cell cell : bufferBorder.cellList()) {
            graph.addVertex(cell2PointMap.get(cell));
        }
        for (Cell cell : bufferBorder.cellList()) {
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
        private UndirectedGraph<Point2D, Line2D> graph;
        private BiMap<Cell, Point2D> map;

        public EdgeReducer(UndirectedGraph<Point2D, Line2D> graph, BiMap<Cell, Point2D> map) {

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
                            canvas.drawCell(oppositeMovedCell, YELLOW);
                            canvas.drawCell(movedCell, RED);
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

    private CachedCellBufferBorder getCoast(Cell startCell, int radius, int depth) {
        return new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(depth, this::isWater),
                Recs.rectangleByCenterPoint(startCell, radius * 2 + 1, radius * 2 + 1)
        );
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


    private CachedCellBufferBorder computeBufferBorder(Rectangle worldRec) {
        CachedCellBufferBorder bufferBorder = new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(
                        20,
                        (x, y) -> worldRec.contains(x, y) && isWater(x, y)
                ),
                worldRec
        );
        bufferBorder.computeAll();
        return bufferBorder;
    }

    private List<Cell> computeCoastRoad(Rectangle worldRec, CachedCellBufferBorder bufferBorder) {
        Collection<Cell> borderCells = new HashSet<>(bufferBorder.cellList());
        List<Cell> coastRoadCells = new ArrayList<>();
        while (!borderCells.isEmpty()) {
            Cell nextBorderCell = borderCells.iterator().next();
            borderCells.remove(nextBorderCell);
            PathTable table = new PathTable(
                    nextBorderCell.x,
                    nextBorderCell.y,
                    (x, y) -> worldRec.contains(x, y) && bufferBorder.isBufferBorder(x, y),
                    MAX_CITY_DEPTH
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

    private boolean isWater(Integer x, Integer y) {
        return noise(x, y) < 128;
    }

    private void coastline() {
        CellBufferBorder bufferBorder = new ChebyshevDistanceCellBufferBorder(
                14,
                this::isWater
        );
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bufferBorder.isBufferBorder(x, y)) {
                    canvas.drawCell(x, y, RED);
                }
            }
        }
    }


    private int noise(int x, int y) {
        return Noise.noise(
                ((double) x) / 32,
                ((double) y) / 32,
                6
        );
    }


    private void drawTerrain() {
        DrawingAlgorithm<Cell> grass = DrawingCell.withColor(Color.GREEN);
        DrawingAlgorithm<Cell> water = DrawingCell.withColor(BLUE);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                canvas.draw(new Cell(i, j), noise(i, j) >= 128 ? grass : water);
            }
        }
    }

    private void drawCoastRoad(List<Cell> roadCells) {
        Iterator<Color> infiniteColors = Colors.infiniteSequence(
                i -> new Color(i * 180 % 256, i * 317 % 256, i * 233 % 256)
        );
        DrawingAlgorithm<Cell> how = DrawingCell.withColor(infiniteColors.next());
        for (Cell cell : roadCells) {
            canvas.draw(cell, how);
        }
    }

}
