package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.CellSet;

@SuppressWarnings("unused")
/**
 * Divides cells into "inner" and "outer" and selects only those "inner" cells that have "outer" cells exactly at
 * certain distance, but not closer.
 */
public class ChebyshevDistanceBufferBorder implements CellSet {
	private int distance;
	private CellSet outerCells;

	public ChebyshevDistanceBufferBorder(
		int distance,
		CellSet outerCells
	) {
		this.outerCells = outerCells;
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
				if (outerCells.contains(i, j)) {
					return false;
				}
			}
		}
		// Check if a set of all cells with Chebyshev distance to x:y == distance
		// contains any outer cells.
		maxX = x + distance;
		for (int i = x - distance; i <= maxX; i++) {
			if (outerCells.contains(i, y - distance)) {
				return true;
			}
			if (outerCells.contains(i, y + distance)) {
				return true;
			}
		}
		maxY = y + distance;
		for (int j = y - distance; j <= maxY; j++) {
			if (outerCells.contains(x - distance, j)) {
				return true;
			}
			if (outerCells.contains(x + distance, j)) {
				return true;
			}
		}
		// If there are no outer cells with Chebyshev distance to x:y == distance,
		// then this cell is not in buffer's  border.
		return false;
	}
}
