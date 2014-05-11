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
import org.tendiwa.noise.SimpleNoiseSource;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;
import org.tendiwa.settlements.SettlementGenerationException;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.awt.Color.*;
import static java.util.stream.IntStream.range;

public class CoastlineDemo implements Runnable {
    public static final int MAX_CITY_DEPTH = 360;
    private final SimpleNoiseSource noise;
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

    public CoastlineDemo() {
        this.noise = (x, y) -> Noise.noise(
                ((double) x) / 32,
                ((double) y) / 32,
                6
        );

    }

    @Override
    public void run() {
        range(18, 19).forEach(i -> {
            System.out.println(i);
            drawTerrain();
            Rectangle worldRec = new Rectangle(0, 0, width, height);
            CachedCellBufferBorder cachedCellBufferBorder = computeDeepBufferBorder(worldRec, i);
            Cell startCell = computeCoastRoad(worldRec, cachedCellBufferBorder).get(0);
            PathTable table;
            table = computeCityShape(
                    worldRec,
                    startCell,
                    getCoast(startCell, radius, i),
                    radius
            );

            computeCityBoundingRoads(table, worldRec, startCell);
            canvas.drawCell(startCell, Color.WHITE);
        });
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
        return new PathTable(
                start.x,
                start.y,
                (x, y) -> worldRec.contains(x, y) && !bufferBorder.isBufferBorder(x, y),
                radius
        ).computeFull();
    }

    private void computeCityBoundingRoads(PathTable table, Rectangle worldRec, Cell startCell) {
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
                new ChebyshevDistanceCellBufferBorder(
                        1,
                        culledTable::isCellComputed
                ),
                culledTable.getBounds()
        ).computeAll();
        assert culledBufferBorder.isBufferBorder(startCell.x, startCell.y);
        canvas.draw(culledTable, DrawingPathTable.withColor(Color.RED));
        canvas.draw(culledBufferBorder, DrawingBoundedCellBufferBorder.withColor(BLUE));
        UndirectedGraph<Point2D, Segment2D> cityGraph = bufferBorderToGraph(culledBufferBorder);
//        canvas.draw(cityGraph, DrawingGraph.withColorAndVertexSize(ORANGE, 1));

        City city = new CityBuilder(cityGraph)
                .withDefaults()
                .withMaxStartPointsPerCycle(200)
                .build();
        canvas.draw(city, new CityDrawer());
    }

    private UndirectedGraph<Point2D, Segment2D> bufferBorderToGraph(CachedCellBufferBorder bufferBorder) {
        UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
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


    /**
     * Computes buffer border used to find city's center.
     *
     * @param worldRec
     *         A Rectangle starting at 0:0 with width and height of the world.
     * @return A buffer border that is
     */
    private CachedCellBufferBorder computeDeepBufferBorder(Rectangle worldRec, int thinBufferDepth) {
        CachedCellBufferBorder bufferBorder = new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(
                        thinBufferDepth + 20,
                        (x, y) -> worldRec.contains(x, y) && isWater(x, y)
                ),
                worldRec
        );
        bufferBorder.computeAll();
        System.out.println(bufferBorder.cellList().size());
        if (bufferBorder.cellList().isEmpty()) {
            throw new SettlementGenerationException("Deep buffer border is so deep it is non-existent");
        } ;
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
        return noise.noise(x, y) < 128;
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


    private void drawTerrain() {
        DrawingAlgorithm<Cell> grass = DrawingCell.withColor(Color.GREEN);
        DrawingAlgorithm<Cell> water = DrawingCell.withColor(BLUE);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                canvas.draw(new Cell(i, j), noise.noise(i, j) >= 128 ? grass : water);
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
