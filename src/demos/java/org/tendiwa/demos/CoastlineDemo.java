package org.tendiwa.demos;

import org.tendiwa.drawing.Colors;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingCell;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.CellBufferBorder;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.CachedCellBufferBorder;
import org.tendiwa.geometry.extensions.ChebyshevDistanceCellBufferBorder;
import org.tendiwa.noise.Noise;
import org.tendiwa.pathfinding.dijkstra.PathTable;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CoastlineDemo {
    private static final int width = 600;
    private static final int height = 400;
    private static TestCanvas canvas;
    private static final int radius = 38;

    public static void main(String[] args) {
        canvas = Demos.createCanvas();
        drawTerrain();
        Rectangle worldRec = new Rectangle(0, 0, width, height);
        CachedCellBufferBorder cachedCellBufferBorder = computeBufferBorder(worldRec);
        List<Cell> roadCells = computeCoastRoad(worldRec, cachedCellBufferBorder);
        Cell startCell = roadCells.get(300);
        PathTable table = computeCityShape(
                worldRec,
                startCell,
                getCoast(startCell, radius),
                radius
        );
        drawPathTable(table);
    }

    private static CachedCellBufferBorder getCoast(Cell startCell, int radius) {
        return new CachedCellBufferBorder(
                new ChebyshevDistanceCellBufferBorder(5, CoastlineDemo::isWater),
                Recs.rectangleByCenterPoint(startCell, radius * 2 + 1, radius * 2 + 1)
        );
    }

    private static PathTable computeCityShape(
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


    private static CachedCellBufferBorder computeBufferBorder(Rectangle worldRec) {
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

    private static List<Cell> computeCoastRoad(Rectangle worldRec, CachedCellBufferBorder bufferBorder) {
        Collection<Cell> borderCells = new HashSet<>(bufferBorder.cellList());
        List<Cell> coastRoadCells = new ArrayList<>();
        while (!borderCells.isEmpty()) {
            Cell nextBorderCell = borderCells.iterator().next();
            borderCells.remove(nextBorderCell);
            PathTable table = new PathTable(
                    nextBorderCell.x,
                    nextBorderCell.y,
                    (x, y) -> worldRec.contains(x, y) && bufferBorder.isBufferBorder(x, y),
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

    private static boolean isWater(Integer x, Integer y) {
        return noise(x, y) < 128;
    }

    private static void coastline() {
        CellBufferBorder bufferBorder = new ChebyshevDistanceCellBufferBorder(
                14,
                CoastlineDemo::isWater
        );
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (bufferBorder.isBufferBorder(i, j)) {
                    canvas.draw(new Cell(i, j), DrawingCell.withColor(Color.RED));
                }
            }
        }
    }


    private static int noise(int x, int y) {
        return Noise.noise(
                ((double) x) / 32,
                ((double) y) / 32,
                6
        );
    }

    private static void drawPathTable(PathTable table) {
        DrawingAlgorithm<Cell> how = DrawingCell.withColor(Color.RED);
        for (Cell cell : table) {
            canvas.draw(cell, how);
        }
    }

    private static void drawTerrain() {
        DrawingAlgorithm<Cell> grass = DrawingCell.withColor(Color.GREEN);
        DrawingAlgorithm<Cell> water = DrawingCell.withColor(Color.BLUE);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                canvas.draw(new Cell(i, j), noise(i, j) >= 128 ? grass : water);
            }
        }
    }

    private static void drawCoastRoad(List<Cell> roadCells) {
        Iterator<Color> infiniteColors = Colors.infiniteSequence(
                i -> new Color(i * 180 % 256, i * 317 % 256, i * 233 % 256)
        );
        DrawingAlgorithm<Cell> how = DrawingCell.withColor(infiniteColors.next());
        for (Cell cell : roadCells) {
            canvas.draw(cell, how);
        }
    }

}
