package org.tendiwa.pathfinding.dijkstra;

import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Cells;
import org.tendiwa.geometry.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class PathTable implements Iterable<Cell> {

    static final int NOT_COMPUTED_CELL = -1;
    private final int startX;
    private final int startY;
    final PathWalker walker;
    private final int maxDepth;
    private final int width;
    int[][] pathTable;
    ArrayList<Cell> newFront;
    int step;
    private final Rectangle bounds;

    public PathTable(int startX, int startY, PathWalker walker, int maxDepth) {
        this.startX = startX;
        this.startY = startY;
        this.walker = walker;
        this.maxDepth = maxDepth;
        this.width = maxDepth * 2 + 1;
        //noinspection SuspiciousNameCombination
        this.bounds = new Rectangle(startX - maxDepth, startY - maxDepth, width, width);

        step = 0;

        this.pathTable = new int[maxDepth * 2 + 1][maxDepth * 2 + 1];

        newFront = new ArrayList<>();
        newFront.add(new Cell(startX, startY));

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                pathTable[i][j] = NOT_COMPUTED_CELL;
            }
        }
        // Zero-wave consists of a single cell, which is path table's start
        pathTable[maxDepth][maxDepth] = 0;
    }

    /**
     * Returns a new Cell {{@link #startX}:{@link #startY}};
     *
     * @return A new Cell.
     */
    public final Cell getStart() {
        return new Cell(startX, startY);
    }

    /**
     * Returns a rectangle in which all cells of this PathTable reside. Note that this rectangle is defined by
     * #startX, #startY and #maxDepth, and not by actually computed cells. More formally, returns a rectangle
     * <pre>
     * {@code
     * new Rectangle(startX - maxDepth, startY - maxDepth, width, width);
     * }
     * </pre>
     *
     * @return A bounding rectangle for this PathTable defined by its #startX, #startY and #maxDepth.
     */
    public final Rectangle getBounds() {
        //noinspection SuspiciousNameCombination
        return bounds;
    }

    /**
     * A getter of #maxDepth. This method is called radius because in Chebyshev metric #maxDepth is radius of a circle
     * that appears to be square in Euclidean metric.
     *
     * @return #maxDepth
     */
    @SuppressWarnings("unused")
    public final int radius() {
        return maxDepth;
    }

    public final PathTable computeFull() {
        boolean computed;
        do {
            computed = nextWave();
        } while (computed);
        return this;
    }

    private boolean nextWave() {
        if (step == maxDepth) {
            return false;
        }
        ArrayList<Cell> oldFront = newFront;
        newFront = new ArrayList<>();
        for (Cell anOldFront : oldFront) {
            int x = anOldFront.getX();
            int y = anOldFront.getY();
            int[] adjactentX = new int[]{x + 1, x, x, x - 1, x + 1, x + 1, x - 1, x - 1};
            int[] adjactentY = new int[]{y, y - 1, y + 1, y, y + 1, y - 1, y + 1, y - 1};
            for (int j = 0; j < 8; j++) {
                int thisNumX = adjactentX[j];
                int thisNumY = adjactentY[j];
                int tableX = thisNumX - startX + maxDepth;
                int tableY = thisNumY - startY + maxDepth;
                computeCell(thisNumX, thisNumY, tableX, tableY);
            }
        }
        step++;
        return true;
    }

    /**
     * Checks if a cell should be stepped on and adds it into newFront if it should. This code is extracted into a
     * method only to be overridden by {@link PostConditionPathTable}.
     *
     * @param thisNumX
     *         X coordinate of a cell in world coordinates.
     * @param thisNumY
     *         Y coordinate of a cell in world coordinates.
     * @param tableX
     *         X coordinate of a cell in table coordinates.
     * @param tableY
     *         Y coordinate of a cell in table coordinates.
     */
    protected void computeCell(int thisNumX, int thisNumY, int tableX, int tableY) {
        if (pathTable[tableX][tableY] == NOT_COMPUTED_CELL && walker.canStepOn(thisNumX, thisNumY)) {
            // Step to cell if character can see it and it is free
            // or character cannot se it and it is not PASSABILITY_NO
            pathTable[tableX][tableY] = step + 1;
            newFront.add(new Cell(thisNumX, thisNumY));
        }
    }

    /**
     * Returns steps of path to a destination cell computed on this path table.
     *
     * @param x
     *         Destination x coordinate.
     * @param y
     *         Destination y coordinate.
     * @return null if path can't be found.
     */
    public final LinkedList<Cell> getPath(int x, int y) {
        if (Math.abs(x - startX) > maxDepth || Math.abs(y - startY) > maxDepth) {
            throw new IllegalArgumentException("Trying to get path to " + x + ":" + y + ". That point is too far from start point " + startX + ":" + startY + ", maxDepth is " + maxDepth);
        }
        while (pathTable[maxDepth + x - startX][maxDepth + y - startY] == NOT_COMPUTED_CELL) {
            // There will be 0 iterations if that cell is already computed
            boolean waveAddedNewCells = nextWave();
            if (!waveAddedNewCells) {
                return null;
            }
        }
        if (x == startX && y == startY) {
            throw new RuntimeException("Getting path to itself");
        }
        LinkedList<Cell> path = new LinkedList<>();
        if (Cells.isNear(startX, startY, x, y)) {
            path.add(new Cell(x, y));
            return path;
        }
        int currentNumX = x;
        int currentNumY = y;
        int cX = currentNumX;
        int cY = currentNumY;
        for (
                int j = pathTable[currentNumX - startX + maxDepth][currentNumY - startY + maxDepth];
                j > 0;
                j = pathTable[currentNumX - startX + maxDepth][currentNumY - startY + maxDepth]
                ) {
            path.addFirst(new Cell(currentNumX, currentNumY));
            int[] adjactentX = {cX, cX + 1, cX, cX - 1, cX + 1, cX + 1, cX - 1, cX - 1};
            int[] adjactentY = {cY - 1, cY, cY + 1, cY, cY + 1, cY - 1, cY + 1, cY - 1};
            for (int i = 0; i < 8; i++) {
                int thisNumX = adjactentX[i];
                int thisNumY = adjactentY[i];
                int tableX = thisNumX - startX + maxDepth;
                int tableY = thisNumY - startY + maxDepth;
                if (tableX < 0 || tableX >= width) {
                    continue;
                }
                if (tableY < 0 || tableY >= width) {
                    continue;
                }
                if (pathTable[tableX][tableY] == j - 1) {
                    currentNumX = adjactentX[i];
                    currentNumY = adjactentY[i];
                    cX = currentNumX;
                    cY = currentNumY;
                    break;
                }
            }
        }
        return path;
    }

    public final boolean isCellComputed(int x, int y) {
        return bounds.contains(x, y) && pathTable[maxDepth + x - startX][maxDepth + y - startY] != NOT_COMPUTED_CELL;
    }

    @Override
/**
 * Iterates over all computed cells.
 */
    public final Iterator<Cell> iterator() {
        return new Iterator<Cell>() {
            private int n = -1;
            private final int maxN = width * width - 1;

            @Override
            public boolean hasNext() {
                return n < maxN;
            }

            @Override
            public Cell next() {
                int x, y;
                do {
                    n++;
                    x = n % width;
                    y = n / width;
                } while (pathTable[x][y] == NOT_COMPUTED_CELL && n < maxN);
                return new Cell(startX - maxDepth + x, startY - maxDepth + y);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
