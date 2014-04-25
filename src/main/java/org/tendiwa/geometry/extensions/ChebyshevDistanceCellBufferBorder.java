package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.CellBufferBorder;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class ChebyshevDistanceCellBufferBorder implements CellBufferBorder {
    private int distance;
    private BiFunction<Integer, Integer, Boolean> outerSideFinder;

    public ChebyshevDistanceCellBufferBorder(int distance, BiFunction<Integer, Integer, Boolean> outerSideFinder) {
        this.outerSideFinder = outerSideFinder;
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be > 0");
        }
        this.distance = distance;
    }

    @Override
    public boolean isBufferBorder(int x, int y) {
        // Check if a square of all cells with
        // Chebyshev distance to x:y<distance doesn't contain outer cells
        int maxX = x + distance - 1;
        int maxY = y + distance - 1;
        for (int i = x - distance + 1; i <= maxX; i++) {
            for (int j = y - distance + 1; j <= maxY; j++) {
                if (outerSideFinder.apply(i, j)) {
                    return false;
                }
            }
        }
        // Check if a set of all cells with Chebyshev distance to x:y == distance
        // contains any outer cells.
        maxX = x + distance;
        for (int i = x - distance; i <= maxX; i++) {
            if (outerSideFinder.apply(i, y - distance)) {
                return true;
            }
            if (outerSideFinder.apply(i, y + distance)) {
                return true;
            }
        }
        maxY = y + distance;
        for (int j = y - distance; j <= maxY; j++) {
            if (outerSideFinder.apply(x - distance, j)) {
                return true;
            }
            if (outerSideFinder.apply(x + distance, j)) {
                return true;
            }
        }
        // If there are no outer cells with Chebyshev distance to x:y == distance,
        // then this cell is not in buffer's  border.
        return false;
    }
}
