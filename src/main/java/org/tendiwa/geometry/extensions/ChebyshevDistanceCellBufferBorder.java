package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.CellSet;

@SuppressWarnings("unused")
/**
 * Divides cells into "inner" and "outer" and selects only those "inner" cells that have "outer" cells exactly at
 * certain distance, but not closer.
 */
public class ChebyshevDistanceCellBufferBorder implements CellSet {
    private int distance;
    private CellSet outerSideFinder;

    public ChebyshevDistanceCellBufferBorder(
            int distance,
            CellSet outerSideFinder
    ) {
        this.outerSideFinder = outerSideFinder;
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be > 0");
        }
        this.distance = distance;
    }

    @Override
    public boolean contains(int x, int y) {
        // Check if a square of all cells with
        // Chebyshev distance to x:y<distance doesn't contain outer cells
        int maxX = x + distance - 1;
        int maxY = y + distance - 1;
        for (int i = x - distance + 1; i <= maxX; i++) {
            for (int j = y - distance + 1; j <= maxY; j++) {
                if (outerSideFinder.contains(i, j)) {
                    return false;
                }
            }
        }
        // Check if a set of all cells with Chebyshev distance to x:y == distance
        // contains any outer cells.
        maxX = x + distance;
        for (int i = x - distance; i <= maxX; i++) {
            if (outerSideFinder.contains(i, y - distance)) {
                return true;
            }
            if (outerSideFinder.contains(i, y + distance)) {
                return true;
            }
        }
        maxY = y + distance;
        for (int j = y - distance; j <= maxY; j++) {
            if (outerSideFinder.contains(x - distance, j)) {
                return true;
            }
            if (outerSideFinder.contains(x + distance, j)) {
                return true;
            }
        }
        // If there are no outer cells with Chebyshev distance to x:y == distance,
        // then this cell is not in buffer's  border.
        return false;
    }
}
